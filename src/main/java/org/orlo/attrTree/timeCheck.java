package org.orlo.attrTree;
//*******时段合法性检验********//
public class timeCheck {
    private int userStartTime, userEndTime, requireStartTime, requireEndTime;

    public boolean checkTime(String userTime, String requireTime){
        userTime = userTime.substring(5);
        String[] uTime = userTime.split("-");
        String[] uStartTime = uTime[0].split(":");
        String[] uEndTime = uTime[1].split(":");
        userStartTime = Integer.parseInt(uStartTime[0]+uStartTime[1]);
        userEndTime = Integer.parseInt(uEndTime[0]+uEndTime[1]);

        requireTime = requireTime.substring(5);
        String[] rTime = requireTime.split("-");
        String[] rStartTime = rTime[0].split(":");
        String[] rEndTime = rTime[1].split(":");
        requireStartTime = Integer.parseInt(rStartTime[0]+rStartTime[1]);
        requireEndTime = Integer.parseInt(rEndTime[0]+rEndTime[1]);
        if(userStartTime >=  requireStartTime && userStartTime <= requireEndTime
                && userEndTime <= requireEndTime && userStartTime >= requireStartTime){
            return true;
        }
        else{
            return false;
        }
    }
}
