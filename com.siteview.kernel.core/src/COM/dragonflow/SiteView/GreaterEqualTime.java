/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * GreaterEqualTime.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

//import com.recursionsw.BinaryPredicate;

/**
 * Comment for <code>GreaterEqualTime</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
//import BinaryPredicate;

// Referenced classes of package COM.dragonflow.SiteView:
// ScheduleEvent

public class GreaterEqualTime implements com.recursionsw.jgl.BinaryPredicate {

    public GreaterEqualTime() {
    }

    public boolean execute(Object obj, Object obj1) {
        return ((ScheduleEvent) obj).getTime() >= ((ScheduleEvent) obj1)
                .getTime();
    }
}
