/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * PingMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>PingMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;
import java.util.ArrayList;
import com.recursionsw.jgl.HashMap;

public class PingMonitor extends AtomicMonitor
{

    static StringProperty pHost;
    static StringProperty pTimeout;
    static StringProperty pSize;
    static StringProperty pStatus;
    static StringProperty pRoundTripTime;
    static StringProperty pPacketsSent;
    static StringProperty pPacketsReceived;
    static StringProperty pPercentGood;
    static StringProperty pPercentFailed;
    static int kInitialPacketCount = 1;
    static int kRetryPacketCount = 3;

    public PingMonitor()
    {
    }

    public String getHostname()
    {
        return getProperty(pHost);
    }

    public String getTestURL()
    {
        return "/SiteView/cgi/go.exe/SiteView?page=ping&host=" + getProperty(pHost) + "&group=" + HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID)));
    }

    protected boolean update()
    {
        String host = getProperty(pHost);
        int timeout = getPropertyAsInteger(pTimeout);
        int size = getPropertyAsInteger(pSize);
        int initialPacketCount = getSettingAsLong("_pingPacketsInitial", kInitialPacketCount);
        int packetRetry = getSettingAsLong("_pingPacketsRetry", kRetryPacketCount);
        int ai[] = Platform.ping(host, timeout, initialPacketCount, size, this);
        if(ai[1] != initialPacketCount && packetRetry != 0)
        {
            initialPacketCount = packetRetry;
            ai = Platform.ping(host, timeout, initialPacketCount, size, this);
        }
        int packetReceived = ai[1];
        int j1 = ai[0];
        currentStatus = "PingMonitor analyzing results...";
        if(stillActive())
        {
            synchronized(this)
            {
                if(packetReceived > 0)
                {
                    setProperty(pStatus, "ok");
                    float f = (float)j1 / (float)packetReceived;
                    if(f < 10F && getSetting("_singleDigitMillis").length() <= 0)
                    {
                        f = 10F;
                    }
                    String s1;
                    if(getSetting("_singleDigitMillis").length() <= 0)
                    {
                        s1 = TextUtils.floatToString(f / 1000F, 2) + " sec";
                    } else
                    {
                        s1 = TextUtils.floatToString(f / 1000F, 3) + " sec";
                    }
                    if(packetReceived != initialPacketCount)
                    {
                        int k1 = initialPacketCount - packetReceived;
                        s1 = k1 + " out of " + initialPacketCount + " missing, " + s1;
                    }
                    setProperty(pPacketsReceived, packetReceived);
                    setProperty(pPercentGood, TextUtils.floatToString(((float)packetReceived / (float)initialPacketCount) * 100F, 0));
                    setProperty(pPercentFailed, TextUtils.floatToString((((float)initialPacketCount - (float)packetReceived) / (float)initialPacketCount) * 100F, 0));
                    setProperty(pRoundTripTime, f);
                    setProperty(pMeasurement, getMeasurement(pRoundTripTime, 200L));
                    setProperty(pStateString, s1);
                } else
                {
                    setProperty(pPacketsReceived, 0);
                    setProperty(pPercentGood, 0);
                    setProperty(pPercentFailed, 0);
                    setProperty(pMeasurement, 0);
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pStatus, "failed");
                    if(j1 == Platform.PING_COMMAND_FAILED)
                    {
                        setProperty(pNoData, "n/a");
                        setProperty(pStateString, "Data Unavailable command failed to execute");
                    } else
                    {
                        setProperty(pStateString, "failed");
                    }
                }
            }
        }
        return true;
    }

    public  ArrayList getLogProperties()
    {
         ArrayList array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        array.add(pPercentGood);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pHost)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            if(TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if(stringproperty == pTimeout)
        {
            if(TextUtils.digits(s) != s.length())
            {
                hashmap.put(stringproperty, "time out must be a number");
            } else
            if(TextUtils.toInt(s) < 100)
            {
                hashmap.put(stringproperty, "time out must be greater than 100 (remember, the timeout is in milliseconds)");
            }
            return s;
        }
        if(stringproperty == pSize)
        {
            if(TextUtils.digits(s) != s.length())
            {
                hashmap.put(stringproperty, "packet size must be a positive number");
            } else
            if(TextUtils.toInt(s) < 1 || TextUtils.toInt(s) > 60000)
            {
                hashmap.put(stringproperty, "packet size must be a number between 1 and 60000");
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String getProperty(StringProperty stringproperty)
        throws NullPointerException
    {
        if(stringproperty == pDiagnosticText)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            } else
            {
                return DIAGNOSTIC_HEADER + diagnosticTraceRoute(getProperty(pHost)) + "";
            }
        } else
        {
            return super.getProperty(stringproperty);
        }
    }

    static 
    {
        pHost = new StringProperty("_hostname", "", "host name");
        pHost.setDisplayText("Host Name", "the IP address or host name that will be pinged (examples: 206.168.191.21 or demo." + Platform.exampleDomain + ")");
        pHost.setParameterOptions(true, 1, false);
        pTimeout = new NumericProperty("_timeout", "5000", "milliseconds");
        pTimeout.setDisplayText("Timeout", "the time out per packet, in milliseconds, to wait for ping replies\n");
        pTimeout.setParameterOptions(true, 2, true);
        pSize = new NumericProperty("_packetSize", "32", "bytes");
        pSize.setDisplayText("Size", "the size, in bytes, of the ping message");
        pSize.setParameterOptions(true, 3, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pStatus = new StringProperty("status", "no data");
        pPercentGood = new PercentProperty("percentGood");
        pPercentGood.setLabel("% packets good");
        pPercentGood.setStateOptions(3);
        pPercentFailed = new PercentProperty("percentFailed");
        pPercentFailed.setLabel("% packets failed");
        pPacketsSent = new NumericProperty("packetsSent");
        pPacketsSent.setLabel("packets sent");
        pPacketsReceived = new NumericProperty("packetsReceived");
        pPacketsReceived.setLabel("packets received");
        StringProperty astringproperty[] = {
            pHost, pTimeout, pSize, pStatus, pRoundTripTime, pPacketsSent, pPacketsReceived, pPercentGood, pPercentFailed
        };
        addProperties("COM.dragonflow.StandardMonitor.PingMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.PingMonitor", Rule.stringToClassifier("percentGood == 0\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.PingMonitor", Rule.stringToClassifier("roundTripTime == n/a\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.PingMonitor", Rule.stringToClassifier("percentGood < 100\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.PingMonitor", Rule.stringToClassifier("percentGood == 100\tgood", true));
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "description", "Pings a device on the network.");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "help", "PingMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "title", "Ping");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "class", "PingMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "target", "_hostname");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "toolName", "Ping");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "toolDescription", "Performs a roundtrip test on the network.");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "topazName", "Ping");
        setClassProperty("COM.dragonflow.StandardMonitor.PingMonitor", "topazType", "System Resources");
    }
}
