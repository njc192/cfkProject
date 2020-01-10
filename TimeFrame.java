public class TimeFrame
{
    private int begTime;
    private int endTime;


    public TimeFrame(String t1, String t2)
    {
        begTime = convert(t1);
        endTime = convert(t2);
    }

    private static int convert(String s)
    {
        if (s == null)
            return 0;
        String [] arr = s.split(":");
        int hr = Integer.parseInt(arr[0]);
        String [] minutesAndampm = arr[1].split(" ");
        int min = Integer.parseInt(minutesAndampm[0]);
        String amOrPm = minutesAndampm[1];
        if (amOrPm.equalsIgnoreCase("pm"))
        {
            if (hr != 12)
            {
                hr += 12;
            }
        }

        return hr*100 + min;
    }

    public String toString()
    {
        return begTime + " to " + endTime;
    }

    public int getBegTime()
    {
        return begTime;
    }

    public void setBegTime(int begTime)
    {
        this.begTime = begTime;
    }

    public int getEndTime()
    {
        return endTime;
    }

    public void setEndTime(int endTime)
    {
        this.endTime = endTime;
    }
}