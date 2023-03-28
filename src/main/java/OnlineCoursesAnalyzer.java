import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> ptcpCountByInst = courses.stream()
                .collect(Collectors.groupingBy(Course::getInstitution,
                        Collectors.summingInt(Course::getParticipants)));
        return ptcpCountByInst;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<Map.Entry<String, String>, Integer> mapMap = courses.stream()
                .collect(Collectors.groupingBy(
                        c -> Map.entry(c.getInstitution(), c.getSubject()),
                        Collectors.summingInt(Course::getParticipants)));

        List<Map.Entry<Map.Entry<String, String>, Integer>> mapList = new ArrayList<>(mapMap.entrySet());
        mapList.sort((o1, o2) -> {
            int firstCompare = o2.getValue().compareTo(o1.getValue());
            if (firstCompare == 0) {
                int secondCompare = o1.getKey().getKey().compareTo(o2.getKey().getKey());
                if (secondCompare == 0) {
                    return o1.getKey().getValue().compareTo(o2.getKey().getValue());
                } else {
                    return secondCompare;
                }
            } else {
                return firstCompare;
            }
        });

        LinkedHashMap<Map.Entry<String, String>, Integer> linkedMapMap = new LinkedHashMap<>();
        mapList.forEach(entry -> linkedMapMap.put(entry.getKey(), entry.getValue()));

        LinkedHashMap<String, Integer> linkedStrMap = new LinkedHashMap<>();
        linkedMapMap.forEach((key, value) -> {
//            String newKey = key.getKey() + "-" + key.getValue().substring(1, key.getValue().length()-1);
            String newKey = key.getKey() + "-" + key.getValue();
            linkedStrMap.put(newKey, value);
        });

        return linkedStrMap;
    }

    public void isCourseIndividual(Course c) {
        String[] instructors = c.getInstructors().split(",");
        c.isIndividual = instructors.length > 1 ? false : true;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        courses.forEach(c -> isCourseIndividual(c));

        Map<String, List<Course>> strListMap = courses.stream()
                .flatMap(person -> Arrays.stream(person.getInstructors().split(", ")).map(name -> new AbstractMap.SimpleEntry<>(name, person)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        List<Map.Entry<String, List<Course>>> strListList = new ArrayList<>(strListMap.entrySet());
//        strListList.sort(Comparator.comparing(Map.Entry::getKey));

        LinkedHashMap<String, List<List<String>>> linkedStrListMap = new LinkedHashMap<>();
        strListList.forEach(entry -> {
            List<String> indpdt = new ArrayList<>();
            List<String> dpdt = new ArrayList<>();
            entry.getValue().forEach(c -> {
                String title = c.getTitle();
                if (c.isIndividual) {
                    if (!indpdt.contains(title)) {
                        indpdt.add(title);
                    }
                } else {
                    if (!dpdt.contains(title)) {
                        dpdt.add(title);
                    }
                }
            });
            Collections.sort(indpdt);
            Collections.sort(dpdt);
            if (indpdt.size() > 0 || dpdt.size() > 0) {
                linkedStrListMap.put(entry.getKey(), List.of(indpdt, dpdt));
            }
        });

        return linkedStrListMap;
    }


    //4
    public List<String> getCourses(int topK, String by) {
//        Map<Map.Entry<String, String>, ? extends Number> idtitleByMap = new HashMap<>();
//        if (by == "hours") {
//            idtitleByMap = courses.stream()
//                    .collect(Collectors.toMap(
//                            course -> Map.entry(course.getNumber(), course.getTitle()),
//                            Course::getTotalHours
//                    ));
//        } else if(by == "participants") {
//            idtitleByMap = courses.stream()
//                    .collect(Collectors.toMap(
//                            course -> Map.entry(course.getNumber(), course.getTitle()),
//                            Course::getParticipants
//                    ));
//        } else {
//            return null;
//        }
//
//        List<Map.Entry<Map.Entry<String, String>, ? extends Number>> idtitleByList = new ArrayList<>(idtitleByMap.entrySet());
//        idtitleByList.sort((o1, o2) -> {
//            double firstCompare = o2.getValue().doubleValue() - o1.getValue().doubleValue();
//            if (firstCompare == 0.0) {
//                return o1.getKey().getValue().compareTo(o2.getKey().getValue());
//            } else {
//                if (firstCompare > 0.0) {
//                    return 1;
//                } else if (firstCompare < 0.0) {
//                    return -1;
//                } else {
//                    return 0;
//                }
//            }
//        });
//
//        List<String> titleList = new ArrayList<>();
//        int i = 0;
//        int j = 0;
//        while (i < topK) {
//            String title = idtitleByList.get(j++).getKey().getValue();
//            if (!titleList.contains(title)) {
//                titleList.add(title);
//                i++;
//            }
//        }
//
//        return titleList;

        List<String> topKCourses = null;
        if (by.equals("hours")) {
            topKCourses = courses.stream()
                    .sorted(Comparator.comparing(Course::getTotalHours).reversed()
                            .thenComparing(Course::getTitle))
                    .map(Course::getTitle)
                    .distinct()
                    .limit(topK)
                    .collect(Collectors.toList());

        } else if (by.equals("participants")) {
            topKCourses = courses.stream()
                    .sorted(Comparator.comparing(Course::getParticipants).reversed()
                            .thenComparing(Course::getTitle))
                    .map(Course::getTitle)
                    .distinct()
                    .limit(topK)
                    .collect(Collectors.toList());
        }
        return topKCourses;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        Pattern pattern = Pattern.compile(courseSubject, Pattern.CASE_INSENSITIVE);

        List<String> result = courses.stream()
                .filter(course -> pattern.matcher(course.getSubject()).find())
                .filter(course -> course.getPercentAudited() >= percentAudited)
                .filter(course -> course.getTotalHours() <= totalCourseHours)
                .map(Course::getTitle)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return result;
    }

    // 6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
//        System.out.println("age: " + age + "\tgender: " + gender + "\tisBachelorOrHigher: " + isBachelorOrHigher + "\n");

        // the latest title for a course number
        Map<String, Course> latestCourses = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber,
                        Collectors.maxBy(Comparator.comparing(Course::getLaunchDate))))
                .values().stream().collect(Collectors.toMap(c -> c.get().getNumber(), c -> c.get()));

        // calculate average similarity (group by number)
        Map<String, Double> averageSimilarities = courses.stream()
                .collect(Collectors.groupingBy(
                        Course::getNumber,
                        Collectors.averagingDouble(c -> c.getSimilarity(age, gender, isBachelorOrHigher))
                ));

        // only latest reserved
        List<CourseSimilarity> courseSimilarities = new ArrayList<>();
        for (Map.Entry<String, Double> entry : averageSimilarities.entrySet()) {
            String number = entry.getKey();
            Double avgSimilarity = entry.getValue();
            if (latestCourses.containsKey(number)) {
                Course latestCourse = latestCourses.get(number);
                CourseSimilarity courseSimilarity = new CourseSimilarity(latestCourse.getTitle(), latestCourse.getTitle(),
                        avgSimilarity);
                courseSimilarities.add(courseSimilarity);
            }
        }

        List<String> recommendedCourses = courseSimilarities.stream()
                .sorted(Comparator.comparing(CourseSimilarity::getSimilarity)
                        .thenComparing(CourseSimilarity::getTitle))
                .map(CourseSimilarity::getTitle)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());


//        recommendedCourses.forEach(System.out::println);

        return recommendedCourses;
    }
}

class CourseSimilarity {
    private String courseNumber;
    private String title;
    private double similarity;

    public CourseSimilarity(String courseNumber, String title, double similarity) {
        this.courseNumber = courseNumber;
        this.title = title;
        this.similarity = similarity;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public String getTitle() {
        return title;
    }

    public double getSimilarity() {
        return similarity;
    }
}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    boolean isIndividual;

    public double getSimilarity(int age, int gender, int isBachelorOrHigher) {
        double similarity = Math.pow((age - medianAge), 2) +
                Math.pow((100*gender - percentMale), 2) +
                Math.pow((100*isBachelorOrHigher - percentDegree), 2);
        return similarity;
    }

    public String getInstitution() {
        return institution;
    }

    public String getNumber() {
        return number;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructors() {
        return instructors;
    }

    public String getSubject() {
        return subject;
    }

    public int getYear() {
        return year;
    }

    public int getHonorCode() {
        return honorCode;
    }

    public int getParticipants() {
        return participants;
    }

    public int getAudited() {
        return audited;
    }

    public int getCertified() {
        return certified;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public double getPercentCertified() {
        return percentCertified;
    }

    public double getPercentCertified50() {
        return percentCertified50;
    }

    public double getPercentVideo() {
        return percentVideo;
    }

    public double getPercentForum() {
        return percentForum;
    }

    public double getGradeHigherZero() {
        return gradeHigherZero;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getMedianHoursCertification() {
        return medianHoursCertification;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public double getPercentMale() {
        return percentMale;
    }

    public double getPercentFemale() {
        return percentFemale;
    }

    public double getPercentDegree() {
        return percentDegree;
    }

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
}