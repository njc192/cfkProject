import java.util.ArrayList;

public class CFKRoom implements Comparable<CFKRoom>
{
    private String titleOfClass;
    private String duration;
    private int sessionNumber;
    private TimeFrame timeOfClass = new TimeFrame("00:00 AM","00:00 AM");
    private String teacher = "Overall Class Choices";
    private String location;
    private ArrayList<String> classDays = new ArrayList<>();
    private ArrayList<String> students = new ArrayList<>();


    public String getTitleOfClass()
    {
        return titleOfClass;
    }

    public void setTitle(String titleOfClass)
    {
        this.titleOfClass = titleOfClass;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public int getSessionNumber()
    {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber)
    {
        this.sessionNumber = sessionNumber;
    }

    public void setTitleOfClass(String titleOfClass)
    {
        this.titleOfClass = titleOfClass;
    }

    public TimeFrame getTimeOfClass()
    {
        return timeOfClass;
    }

    public void setTimeOfClass(TimeFrame timeOfClass)
    {
        this.timeOfClass = timeOfClass;
    }

    public String getTeacher()
    {
        return teacher;
    }

    public void setTeacher(String teacher)
    {
        this.teacher = teacher;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public ArrayList<String> getClassDays()
    {
        return classDays;
    }

    public void setClassDays(ArrayList<String> classDays)
    {
        this.classDays = classDays;
    }

    public ArrayList<String> getStudents()
    {
        return students;
    }

    @Override
    public String toString()
    {
        StringBuilder studnts = new StringBuilder();
        for (int i = 0; i < students.size(); i++)
        {
            studnts.append(students.get(i));
            studnts.append(" \n");
        }


        return getTitleOfClass() + "\n" +getTeacher() + " Session:" + getSessionNumber() + " " +   getTimeOfClass();
    }

    @Override
    public int compareTo(CFKRoom cfkRoom)
    {
        int s1 = getSessionNumber();
        int s2 = cfkRoom.getSessionNumber();


        if (s1 < s2)
            return -1;

        else if (s1 == s2)
        {
            TimeFrame tf1 = getTimeOfClass();
            TimeFrame tf2 = cfkRoom.getTimeOfClass();
            if (tf1.getBegTime() < tf2.getBegTime())
                return -1;
            else if (tf1.getBegTime() > tf2.getBegTime())
                return 1;
            else
            {
                if (tf1.getEndTime() < tf2.getEndTime())
                    return -1;
                else if (tf1.getEndTime() > tf2.getEndTime())
                    return 1;
                else
                    return 0;
            }
        }
        else
            return 1;
    }

}
