import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class AttendanceSheetReader
{
    private static ArrayList<String> info = new ArrayList<>();
    private static SortedMap<String,ArrayList<CFKRoom>> studentMap;
    private static SortedMap<String,ArrayList<CFKRoom>> teacherMap;
    private final static String TEACHERS = "C:\\Code\\ExcelReading\\Me\\Cuesta\\Project\\Output\\Teachers\\teachers.xlsx";
    private final static String SUMMARY = "C:\\Code\\ExcelReading\\Me\\Cuesta\\Project\\Output\\Summary";
    private final static String STUDENTS = "C:\\Code\\ExcelReading\\Me\\Cuesta\\Project\\Output\\Students";
    private final static String TEACHERROOMLINKS = "C:/Code/ExcelReading/Me/Cuesta/Project/Output/TeacherRoomLinks/";
    private final static String ROOMS = "C:/Code/ExcelReading/Me/Cuesta/Project/Output/Rooms/";
    private final static String READLOCTATION = "C:\\Code\\ExcelReading\\Me\\Cuesta\\Project\\ExcelFiles\\attendance-sheets.xlsx";
    public AttendanceSheetReader()
    {
        studentMap = new TreeMap<>();
        teacherMap = new TreeMap<>();
        formatInfo();
        readInfo();
    }

    public static void main(String[] args)
    {
        new AttendanceSheetReader();
    }



    public static SortedMap<String,ArrayList<CFKRoom>> getStudentMap()
    {
        return new TreeMap<>(studentMap);
    }
    public static SortedMap<String,ArrayList<CFKRoom>> getTeacherMap()
    {
        return new TreeMap<>(teacherMap);
    }

    public static void formatInfo()
    {
        try (FileInputStream f = new FileInputStream(READLOCTATION))
        {
            XSSFWorkbook workbook = new XSSFWorkbook(f);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.rowIterator();
            Iterator<Cell> cellIterator;

            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                cellIterator = row.cellIterator();
                while(cellIterator.hasNext())
                {
                    String val = cellIterator.next().toString();

                    if (!val.isEmpty())
                        info.add(val);
                }
            }
            for (int i = 0; i < info.size(); i++)
            {
                try
                {
                    Double.parseDouble(info.get(i));
                    info.remove(i);
                }catch (NumberFormatException e)
                {

                }
            }
       }catch(Exception fnf)
        {
            fnf.printStackTrace();
        }
    }

    private static void readInfo()
    {
        CFKRoom cfkroom = null;
        for (int i = 0; i < info.size(); i++)
        {
            //Title of Class and Session number
            if (info.get(i).contains("CFK - Session"))
            {
                cfkroom = new CFKRoom();
                String [] sessInfo = info.get(i).split(" ");
                cfkroom.setSessionNumber(Integer.parseInt(sessInfo[3]));
//                System.out.println("-------------------> Session:" + sessInfo[2] + " " + sessInfo[3]);
//                System.out.print("------------------->  Title:");


                int j = 4;

                StringBuilder sb = new StringBuilder();
                do
                {
                    sb.append(sessInfo[j++]);
                    sb.append(" ");
//                    System.out.print(sessInfo[j++]);
                } while(!sessInfo[j].equals("-"));
//                System.out.println("-");
                sb.append("-");
                String title = sb.toString();
                cfkroom.setTitle(title);


            }

            //Duration
            else if (info.get(i).equals("Dates:"))
            {
//                System.out.print("-------------------> Duration ");
                i++;
                StringBuilder sb = new StringBuilder();
                int counter = 0;
                while(counter < 3)
                {
                    sb.append(info.get(i++) + " ");
//                    System.out.print(info.get(i++) + " ");
                    counter++;
                }
//                System.out.println();
                cfkroom.setDuration(sb.toString());
            }

            //Time
            else if (info.get(i).contains("Time"))
            {
                String t1 = info.get(i+1);
                String t2 = info.get(i+3);
                TimeFrame tf = new TimeFrame(t1,t2);

                cfkroom.setTimeOfClass(tf);
////                System.out.print("------------------->  Time ");
//                i++;
//                StringBuilder sb = new StringBuilder();
//                int counter = 0;
//                while (counter < 3)
//                {
////                    sb.append(info.get(i++));
////                    System.out.print(info.get(i++) + " ");
//                    counter++;
//                }
////                System.out.println();
////                cfkroom.setTimeOfClass(sb.toString());
            }
            //Teacher
            else if (info.get(i).contains("Primary Instructor:"))
            {
                String teacher = info.get(++i);

               //add teacher to the room
                cfkroom.setTeacher(teacher);

                //add teacher to the map
                if (teacherMap.get(teacher) != null)
                {
                    ArrayList<CFKRoom> arr = teacherMap.get(teacher);
                    arr.add(cfkroom);
                    teacherMap.put(teacher,arr);
                }
                else
                {
                    ArrayList<CFKRoom> arr = new ArrayList<>();
                    arr.add(cfkroom);
                    teacherMap.put(teacher,arr);
                }

//                System.out.println("-------------------> Teacher  " + info.get(++i));
            }
            //Location
            else if(info.get(i).contains("Location"))
            {
                cfkroom.setLocation(info.get(++i));
//                System.out.println("-------------------> Location " + info.get(++i));
            }
            else if(info.get(i).contains("Qty"))
            {
                i++;
                //ClassDays
                while (info.get(i).contains("Jun") || info.get(i).contains("Jul"))
                {
//                    System.out.println("------------------->  ClassDay: " + info.get(i++));
                    cfkroom.getClassDays().add(info.get(i++));
                }
                //Students
                while(!info.get(i).contains("Aug") && i < info.size()-1)
                {

                    if (!info.get(i).contains("0") && !info.get(i).contains("Event"))
                    {
                        cfkroom.getStudents().add(info.get(i));

                        if (studentMap.get(info.get(i)) != null)
                        {
                            String name = info.get(i);
                            ArrayList<CFKRoom> room = studentMap.get(name);
                            room.add(cfkroom);
                            studentMap.put(name,room);
                        }
                        else
                        {
                            String name = info.get(i);
                            ArrayList<CFKRoom> room = new ArrayList<>();
                            room.add(cfkroom);
                            studentMap.put(name,room);
                        }
//                        System.out.println(info.get(i));
                    }

                    i++;
                }
                if (i == info.size() -1)
                {
                    int val = info.size()-1;
                    cfkroom.getStudents().add(info.get(val));
                    if (studentMap.get(info.get(val)) != null)
                    {
                        String name = info.get(val);
                        ArrayList<CFKRoom> arrayOfRooms = studentMap.get(name);
                        arrayOfRooms.add(cfkroom);
                        studentMap.put(name,arrayOfRooms);
                    }
                    else
                    {
                        String name = info.get(val);
                        ArrayList<CFKRoom> arrayOfRooms = new ArrayList<>();
                        arrayOfRooms.add(cfkroom);
                        studentMap.put(name,arrayOfRooms);
                    }
                }
            }

            //mostly meaningless info
//            else
//                System.out.println(info.get(i));
        }
    }

    private static void readRooms() // used to read hashmaps
    {
        Set<Map.Entry<String,ArrayList<CFKRoom>>> set = teacherMap.entrySet();

        for (Map.Entry<String,ArrayList<CFKRoom>> val : set)
        {
            System.out.print(val.getKey() + ": " + val.getValue().size() + "\n");
            ArrayList<CFKRoom> room = val.getValue();
            //sortByTime(room);
            for (int i = 0; i < room.size(); i++)
            {
                System.out.println(room.get(i));
            }

            System.out.println("\n");
        }
    }

    public static List<Object> getTeachers()
    {
        if (teacherMap != null)
        {
            List<Object> arr = new ArrayList<>();
            Set<Map.Entry<String,ArrayList<CFKRoom>>> set = teacherMap.entrySet();

            for (Map.Entry<String,ArrayList<CFKRoom>> val : set)
            {
                arr.add(val.getKey());
            }
            return arr;
        }
        return new ArrayList<>();
    }

    public static String createJsonFile()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(teacherMap);
            System.out.println(json);
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            String students = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(studentMap);
//            System.out.println(students);
            return json;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static void sortByTime(ArrayList<CFKRoom> room)
    {
        for (int i = 1; i < room.size(); i++)
        {
            CFKRoom tmp = room.get(i);
            int j = i;
            for (; j > 0 && tmp.compareTo(room.get(j -1)) < 0 ; j--)
            {
                room.set(j,room.get(j-1));
            }
            room.set(j,tmp);
        }
    }

    private static void createTeacherSheet()
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Teachers");
        Set<Map.Entry<String,ArrayList<CFKRoom>>> set = teacherMap.entrySet();

        int rownum = 0;
        Row row;

        for (Map.Entry<String,ArrayList<CFKRoom>> val : set)
        {
            row = sheet.createRow(rownum++);
            String teacher = val.getKey();
            ArrayList<CFKRoom> arr = val.getValue();

            Cell cell = row.createCell(0);
            cell.setCellValue(teacher);
            sheet.autoSizeColumn(0);

            URI uri = createRoomSheet(teacher.hashCode(),arr);
            cell.setCellStyle(createHyperLink(workbook,cell,uri));

        }

        try(FileOutputStream fos = new FileOutputStream(new File(TEACHERS)))
        {
            //write the workbook in the file system

            workbook.write(fos);
            System.out.println("TEACHERS FILE SHOULD EXIST");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static URI createRoomSheet(int hash,ArrayList<CFKRoom> arr)
    {
        URI uri = createURI(TEACHERROOMLINKS + hash + ".xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Rooms");
        int rownum = 0;
        Row row;
        for (int i = 0; i < arr.size(); i++)
        {
            CFKRoom room = arr.get(i);

            int cellnum = 0;
            row = sheet.createRow(rownum++);
            Cell cell = row.createCell(cellnum++);

            //Title of room
            cell.setCellValue(room.getTitleOfClass());
            //todo set underlined

            URI link = createSpecificSheet(room);

            cell.setCellStyle(createHyperLink(workbook,cell,link));
            cell = row.createCell(cellnum++);
            cell.setCellValue(room.getDuration());

            cell = row.createCell(cellnum++);
            cell.setCellValue("Session: " + room.getSessionNumber());
        }

        for (int i = 0; i < 50; i++)
        {
            sheet.autoSizeColumn(i);
        }


        try( FileOutputStream fos = new FileOutputStream(new File(uri)))
        {
           workbook.write(fos);
           System.out.println("Created room links");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return uri;
    }


    private static URI createSpecificSheet(CFKRoom cfkRoom)
    {
        URI uri = createURI(ROOMS + cfkRoom.hashCode()+".xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        int rownum = 0;
        Row row = sheet.createRow(rownum++);

        int cellnum = 0;
        Cell cell = row.createCell(cellnum++);


        cell.setCellValue(cfkRoom.getTitleOfClass());
        cell = row.createCell(cellnum++);
        cell.setCellValue(cfkRoom.getDuration());



        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Session: " + cfkRoom.getSessionNumber());

        cellnum = 0;
        row = sheet.createRow(rownum++);
        cell = row.createCell(cellnum++);
        cell.setCellValue("Enrollee Name");
        ArrayList<String > classDays = cfkRoom.getClassDays();
        for (int i = 0 ; i < classDays.size(); i++)
        {
            cell = row.createCell(cellnum++);
            cell.setCellValue(classDays.get(i));
            cellnum = cellnum+3;
        }

        cellnum = 0;
        ArrayList<String> students = cfkRoom.getStudents();
        for (int i = 0; i < students.size(); i++)
        {
            row = sheet.createRow(rownum++);
            cell = row.createCell(cellnum++);
            cell.setCellValue(students.get(i));

            for (int j = 0; j < classDays.size(); j++)
            {
                Cell cell1 = row.createCell(cellnum++);
                cell1.setCellValue("P");

                Cell cell2 = row.createCell(cellnum++);
                cell2.setCellValue("A");

                Cell cell3 = row.createCell(cellnum++);
                cell3.setCellValue("T");

                Cell cell4 = row.createCell(cellnum++);
                cell1.setCellStyle(createHyperLink(workbook,cell1,cell4));
                cell2.setCellStyle(createHyperLink(workbook,cell2,cell4));
                cell3.setCellStyle(createHyperLink(workbook,cell3,cell4));



            }
            cellnum = 0;
        }

        sheet.autoSizeColumn(0);


        try( FileOutputStream fos = new FileOutputStream(new File(uri)))
        {
            workbook.write(fos);
            System.out.println("CFKROOM CREATED");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return uri;
    }

    private static XSSFCellStyle createHyperLink(XSSFWorkbook workbook, Cell linkCell, URI uri)
    {
        //creating the hyperlink to the new file with the students info
        CreationHelper creationHelper = workbook.getCreationHelper();
        XSSFCellStyle hypLinkStyle = workbook.createCellStyle();
        XSSFFont hypLinkFont = workbook.createFont();
        hypLinkFont.setUnderline(XSSFFont.U_SINGLE);
        hypLinkFont.setColor(XSSFFont.DEFAULT_FONT_COLOR);
        hypLinkStyle.setFont(hypLinkFont);


        XSSFHyperlink link = (XSSFHyperlink)creationHelper.createHyperlink(HyperlinkType.FILE);
        link.setAddress(uri.toString());
        linkCell.setHyperlink(link);
        linkCell.setCellStyle(hypLinkStyle);
        return hypLinkStyle;
    }

    private static XSSFCellStyle createHyperLink(XSSFWorkbook wb,Cell linkCell, Cell c4)
    {
        CreationHelper creationHelper = wb.getCreationHelper();
        XSSFCellStyle hypLinkStyle = wb.createCellStyle();
        XSSFFont hypLinkFont = wb.createFont();
        hypLinkFont.setUnderline(XSSFFont.U_SINGLE);
        hypLinkFont.setColor(XSSFFont.DEFAULT_FONT_COLOR);
        hypLinkStyle.setFont(hypLinkFont);

        XSSFHyperlink link = (XSSFHyperlink)creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
        link.setAddress(c4.getSheet().getSheetName()+"!"+c4.getAddress());
        linkCell.setHyperlink(link);
        linkCell.setCellStyle(hypLinkStyle);
        return hypLinkStyle;
    }


    public static URI createURI(String fileLocation)
    {
        try
        {
            URI uri = new URI("file:///" + fileLocation.replaceAll(" ", "%20"));
            return uri;
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}


