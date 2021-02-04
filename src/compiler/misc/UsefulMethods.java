package compiler.misc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsefulMethods {
    public static int countChar(String str, char c)
    {
        if ( str.contains("()") && c==',')
            return -1;

        int count = 0;
        for(int i=0; i < str.length(); i++)
        {    if(str.charAt(i) == c)
            count++;
        }


        return count;
    }
    public static ArrayList<String> getAllMatches(String text, String regex) {
        ArrayList<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
        while(m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }
    public static boolean haveDuplicate(String[] item) {
        boolean output = false;
        for (int j = 0; j < item.length; j++)
            for (int k = j + 1; k < item.length; k++)
                if (k != j && item[k].equals(item[j])) {
                    output = true;
                    break;
                }
        return output;
    }

}
