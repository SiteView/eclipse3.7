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
import java.util.Properties;

import java.util.ArrayList;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// CGI, testAction

public class testPage extends COM.dragonflow.Page.CGI {

    public testPage() {
    }

    public void printBody() {
        outputStream.println("<pre>");
        try {
            StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils
                    .readFile("f:/www/htdocs/siteseer_legal_agreement.htm");
            ArrayList array = COM.dragonflow.SiteView.Platform.split('\n',
                    stringbuffer.toString());
            String s1;
            for (Enumeration enumeration1 = (Enumeration) array.iterator(); enumeration1
                    .hasMoreElements(); outputStream.println("+\"" + s1.trim()
                    + "\"")) {
                s1 = (String) enumeration1.nextElement();
                s1 = COM.dragonflow.Utils.TextUtils.replaceChar(s1, '"', "\\\"");
            }

        } catch (java.lang.Exception exception) {
        }
        outputStream.println("root="
                + COM.dragonflow.SiteView.Platform.getRoot());
        java.util.Properties properties = new Properties(java.lang.System
                .getProperties());
        properties.list(outputStream);
        outputStream.flush();
        for (Enumeration enumeration = request.getVariables(); enumeration
                .hasMoreElements();) {
            String s = (String) enumeration.nextElement();
            Enumeration enumeration2 = request.getValues(s);
            while (enumeration2.hasMoreElements()) {
                outputStream.println(s + "=" + enumeration2.nextElement());
            }
        }

        outputStream.println("</pre>");
        if (request.getValue("schedulerTest").length() > 0) {
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                    .currentSiteView();
            COM.dragonflow.Page.testAction testaction = new testAction();
            siteviewgroup.monitorScheduler.scheduleRepeatedPeriodicAction(
                    testaction, 1L, 1L);
            ArrayList array1 = new ArrayList();
            ArrayList array2 = new ArrayList();
            ArrayList array3 = new ArrayList();
            while (true) {
                siteviewgroup.adjustGroups(array1, array2, array3,
                        new HashMap());
            } 
        } else {
            return;
        }
    }
}
