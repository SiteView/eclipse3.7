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

import java.util.ArrayList;

public interface treeInterface {

    public abstract boolean process(boolean flag, boolean flag1,
            StringBuffer stringbuffer);

    public abstract ArrayList selected();
}
