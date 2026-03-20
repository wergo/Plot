package at.ofai.music.plot;

/**
 * Werner Goebl's utitlity class. All sorts of routines...
 *
 * @author wernerg
 *
 */
public class U {

    /**
     * Translates a typical file name into a String inserting reverse solidi
     * ("\") etc. befor latex-like characters (at the moment: "_" for subscript;
     * "^" for superscript).
     *
     * @param str
     * @return the corrected string
     */
    public static String t_t(String str) {
        int i = 0;
        while (i < str.length()) {
            if ((str.codePointAt(i) == 0x5F) || // 0x5F underscore
                    (str.codePointAt(i) == 0x5E)) { // 0x5E circumflex
                String begStr = "";
                if (i > 0) {
                    begStr = str.substring(0, i);
                }
                String endStr = str.substring(i);
                //str = begStr + "\\" + endStr;
                str = begStr + (char) 0x5C + endStr;
                i++;
            }
            i++;
        }
        return str;
    } // t_t

    public static String getBase(String fileName) {
        int ind = fileName.length();
        int i = fileName.lastIndexOf("/");
        if (i > 0) {
            ind = i + 1;
        }
        i = fileName.lastIndexOf("\\");
        if (i > 0) {
            ind = i + 1;
        }
        return fileName.substring(0, ind);
    }
} // class U