package org.telegram.telegrios;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;

/** Misc Telegrios helpers: snippets, inline calculator, URL safety, reg-date estimate. */
public final class TelegriosUtils {

    private TelegriosUtils() {}

    /** Word-joiner used for "empty messages". */
    public static final String INVISIBLE_CHAR = "\u2060";

    public static void showToast(Context ctx, String text) {
        try {
            Toast.makeText(ctx != null ? ctx : ApplicationLoader.applicationContext, text, Toast.LENGTH_SHORT).show();
        } catch (Throwable ignore) {}
    }

    // ---- inline calculator: "=25*48" -> "1200" ----

    public static String tryEvaluate(String text) {
        if (!TelegriosConfig.isInlineCalc()) return null;
        String t = text.trim();
        if (!t.startsWith("=") || t.length() < 2) return null;
        String expr = t.substring(1).replace(",", ".").replace("\u00A0", "").replace(" ", "");
        if (!expr.matches("[0-9+\\-*/%^().]+")) return null;
        try {
            double result = new ExprParser(expr).parse();
            if (Double.isNaN(result) || Double.isInfinite(result)) return null;
            if (result == Math.rint(result) && Math.abs(result) < 1e15) {
                return String.valueOf((long) result);
            }
            String s = String.valueOf(result);
            if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);
            return s;
        } catch (Throwable ignore) {
            return null;
        }
    }

    /** Minimal recursive-descent arithmetic parser: + - * / % ^ ( ) unary minus. */
    static final class ExprParser {
        private final String s;
        private int pos;

        ExprParser(String s) { this.s = s; }

        double parse() {
            double v = parseExpr();
            if (pos < s.length()) throw new IllegalArgumentException("trailing");
            return v;
        }

        private double parseExpr() {
            double v = parseTerm();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '+') { pos++; v += parseTerm(); }
                else if (c == '-') { pos++; v -= parseTerm(); }
                else return v;
            }
            return v;
        }

        private double parseTerm() {
            double v = parsePow();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '*') { pos++; v *= parsePow(); }
                else if (c == '/') { pos++; v /= parsePow(); }
                else if (c == '%') { pos++; v %= parsePow(); }
                else return v;
            }
            return v;
        }

        private double parsePow() {
            double v = parseUnary();
            while (pos + 1 < s.length() && s.charAt(pos) == '^' || (pos < s.length() && s.charAt(pos) == '^')) {
                if (pos < s.length() && s.charAt(pos) == '^') {
                    pos++;
                    v = Math.pow(v, parseUnary());
                }
            }
            return v;
        }

        private double parseUnary() {
            if (pos < s.length() && s.charAt(pos) == '-') { pos++; return -parseUnary(); }
            if (pos < s.length() && s.charAt(pos) == '+') { pos++; return parseUnary(); }
            return parseAtom();
        }

        private double parseAtom() {
            if (pos < s.length() && s.charAt(pos) == '(') {
                pos++;
                double v = parseExpr();
                if (pos >= s.length() || s.charAt(pos) != ')') throw new IllegalArgumentException("paren");
                pos++;
                return v;
            }
            int start = pos;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
            if (start == pos) throw new IllegalArgumentException("number");
            return Double.parseDouble(s.substring(start, pos));
        }
    }

    // ---- text snippets: {":sm": "Some text"} ----

    public static String applySnippets(String text) {
        if (!TelegriosConfig.isSnippets()) return text;
        try {
            JSONObject obj = new JSONObject(TelegriosConfig.getSnippetsData());
            java.util.Iterator<String> it = obj.keys();
            String result = text;
            while (it.hasNext()) {
                String k = it.next();
                if (result.contains(k)) {
                    result = result.replace(k, obj.getString(k));
                }
            }
            return result;
        } catch (Throwable t) {
            return text;
        }
    }

    // ---- anti-phishing heuristics ----

    public static boolean isSuspiciousUrl(String url) {
        if (!TelegriosConfig.isAntiPhishing()) return false;
        if (url == null) return false;
        String u = url.toLowerCase();
        if (u.startsWith("http://") && !u.contains("localhost")) return true;
        if (u.contains("xn--")) return true; // punycode (homoglyph domains)
        String[] shorteners = {"bit.ly", "tinyurl.com", "t.co/", "goo.gl", "is.gd", "cutt.ly", "rb.gy", "shorturl.at", "tiny.cc"};
        for (String s : shorteners) {
            if (u.contains(s)) return true;
        }
        // raw IP host
        try {
            String host = u.replaceFirst("^https?://", "").split("[/:]")[0];
            if (host.matches("[0-9]{1,3}(\\.[0-9]{1,3}){3}")) return true;
        } catch (Throwable ignore) {}
        return false;
    }

    // ---- approximate registration date from user ID ----

    public static long estimateRegDateUnix(long userId) {
        long ts = userId >> 32;
        long now = System.currentTimeMillis() / 1000L;
        if (ts > 1350000000L && ts <= now) return ts;
        return 0;
    }

    // ---- @all mention builder ----

    public static String buildMentionAll(java.util.List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) return "@admin";
        StringBuilder sb = new StringBuilder();
        for (String u : usernames) {
            if (sb.length() > 3500) { sb.append(" ..."); break; }
            sb.append('@').append(u).append(' ');
        }
        return sb.toString().trim();
    }
}
