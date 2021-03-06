/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import java.util.Enumeration;

import java.util.ArrayList;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage

public class settingsPrefsPage extends COM.dragonflow.Page.prefsPage {

    public settingsPrefsPage() {
    }

    public String saveAdditionalPrefs(String s,
            String s1, HashMap hashmap) {
        String s2 = "_nextAdditional" + s + "ID";
        String s3 = "_additional" + s;
        try {
            HashMap hashmap1 = getSettings();
            if (s1.startsWith("new")) {
                s1 = (String) hashmap1.get(s2);
                if (s1 == null) {
                    s1 = "1";
                }
                hashmap1.put(s2, COM.dragonflow.Utils.TextUtils.increment(s1));
            }
            hashmap.put("_id", s1);
            ArrayList array = new ArrayList();
            boolean flag = false;
            for (Enumeration enumeration = COM.dragonflow.Page.settingsPrefsPage
                    .getValues(hashmap1, s3); enumeration.hasMoreElements();) {
                HashMap hashmap2 = COM.dragonflow.Utils.TextUtils
                        .stringToHashMap((String) enumeration
                                .nextElement());
                if (COM.dragonflow.Page.settingsPrefsPage.getValue(hashmap2,
                        "_id").equals(s1)) {
                    array.add(hashmap);
                    flag = true;
                } else {
                    array.add(hashmap2);
                }
            }

            if (!flag) {
                array.add(hashmap);
            }
            hashmap1.remove(s3);
            for (int i = 0; i < array.size(); i++) {
                hashmap1.add(s3, COM.dragonflow.Utils.TextUtils
                        .hashMapToOrderedString((HashMap) array.get(i)));
            }

            saveSettings(hashmap1);
        } catch (java.io.IOException ioexception) {
            printError("The preferences could not be saved",
                    "master.config file could not be saved", "10");
        }
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @return
     */
    public HashMap findAdditionalSetting(String s, HashMap hashmap) {

        HashMap hashmap1 = getSettings();
        String s1 = "_additional" + s;
        Enumeration enumeration = COM.dragonflow.Page.settingsPrefsPage.getValues(hashmap1, s1);
        HashMap hashmap2;
        boolean flag;
        while (enumeration.hasMoreElements()) {
            hashmap2 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String) enumeration.nextElement());
            Enumeration enumeration1 = hashmap.keys();
            flag = true;
            String s2;
            while (enumeration1.hasMoreElements()) {
                s2 = (String) enumeration1.nextElement();
                if (!COM.dragonflow.Page.settingsPrefsPage.getValue(hashmap, s2).equals(COM.dragonflow.Page.settingsPrefsPage.getValue(hashmap2, s2))) {
                    flag = false;
                }
                if (flag && COM.dragonflow.Page.settingsPrefsPage.getValue(hashmap2, "_disabled").length() == 0) {
                    return hashmap2;
                }
            }
        }

        return null;
    }

    public void deleteAdditionalSetting(String s, String s1) {
        String s2 = "_additional" + s;
        HashMap hashmap = getSettings();
        ArrayList array = new ArrayList();
        Enumeration enumeration = COM.dragonflow.Page.settingsPrefsPage
                .getValues(hashmap, s2);
        while (enumeration.hasMoreElements()) {
            HashMap hashmap1 = COM.dragonflow.Utils.TextUtils
                    .stringToHashMap((String) enumeration
                            .nextElement());
            if (!COM.dragonflow.Page.settingsPrefsPage.getValue(hashmap1, "_id")
                    .equals(s1)) {
                array.add(hashmap1);
            }
        } 
        
        hashmap.remove(s2);
        for (int i = 0; i < array.size(); i++) {
            hashmap.add(s2, COM.dragonflow.Utils.TextUtils
                    .hashMapToOrderedString((HashMap) array.get(i)));
        }

        try {
            saveSettings(hashmap);
        } catch (java.io.IOException ioexception) {
            printError("The preferences could not be saved",
                    "master.config file could not be saved", "10");
        }
    }

    HashMap getAdditionalSettings(String s, String s1) {
        HashMap hashmap = null;
        if (s1.length() > 0) {
            String s2 = getSettingsGroup().getAdditionalSettings(s,
                    s1);
            if (s2.length() > 0) {
                hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap(s2);
            }
        }
        return hashmap;
    }
}
