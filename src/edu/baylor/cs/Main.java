package edu.baylor.cs;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        List<Study> studies = new ArrayList<>();
        File file = new File("E:\\Programs\\mapping-study\\src.txt");
        Scanner scanner = new Scanner(new FileInputStream(file));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] tokens = line.split(" - ");
            if(tokens.length != 4){
                System.err.println("Invalid entry - " + line);
                return;
            }

            String name = tokens[0];
            List<Method> methods = Arrays.stream(tokens[1].split(" / ")).map(x -> Method.valueOf(x.toUpperCase())).collect(Collectors.toList());
            OpenSource openSource = OpenSource.valueOf(tokens[2].toUpperCase());
            List<String> languages = Arrays.asList(tokens[3].split(" / "));

            studies.add(new Study(name, methods, openSource, languages));
        }

        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        System.setOut(out);

        studies.forEach(System.out::println);

        System.out.println("\nTotal Count - " + studies.size());

        System.out.println("\n----METHOD----");
        List<MethodResult> methodResults = new ArrayList<>();

        // Generate results
        for(Method method : Method.values()){
            long count = studies.stream().filter(x -> x.getMethods().contains(method)).count();
            methodResults.add(new MethodResult(method, count, studies.size()));
        }
        methodResults = methodResults.stream().sorted((x,y) -> (int) (y.getCount() - x.getCount())).collect(Collectors.toList());

        // Print results
        FileWriter methodFileWriter = new FileWriter("method.csv");
        for(MethodResult methodResult : methodResults){
            System.out.println("\t" + methodResult);
            methodFileWriter.write(methodResult.toCSV() + "\n");
        }
        methodFileWriter.close();

        System.out.println("\n----OPEN SOURCE----");
        List<OpenSourceResult> openSourceResults = new ArrayList<>();

        // Generate results
        for(OpenSource openSource : OpenSource.values()){
            long count = studies.stream().filter(x -> x.getOpenSource().equals(openSource)).count();
            openSourceResults.add(new OpenSourceResult(openSource, count, studies.size()));
        }
        openSourceResults = openSourceResults.stream().sorted((x,y) -> (int) (y.getCount() - x.getCount())).collect(Collectors.toList());

        // Print results
        FileWriter openSourceFileWriter = new FileWriter("openSource.csv");
        for(OpenSourceResult openSourceResult : openSourceResults){
            System.out.println("\t" + openSourceResult);
            openSourceFileWriter.write(openSourceResult.toCSV() + "\n");
        }
        openSourceFileWriter.close();

        // Generate language set
        Set<String> languages = new HashSet<>();
        for(Study study : studies){
            languages.addAll(study.getLanguages());
        }

        System.out.println("\n----LANGUAGES----");
        List<LanguageResult> languageResults = new ArrayList<>();

        // Generate results
        for(String language : languages){
            long count = studies.stream().filter(x -> x.getLanguages().contains(language)).count();
            languageResults.add(new LanguageResult(language, count, studies.size()));
        }
        languageResults = languageResults.stream().sorted((x,y) -> (int) (y.getCount() - x.getCount())).collect(Collectors.toList());

        // Print results
        FileWriter languageFileWriter = new FileWriter("language.csv");
        for(LanguageResult languageResult : languageResults){
            System.out.println("\t" + languageResult);
            languageFileWriter.write(languageResult.toCSV() + "\n");
        }
        languageFileWriter.close();

        System.out.println("\n----TABLE_ROWS----");
        for(Study study : studies){
            System.out.println(study.tableRow());
        }
    }

    private static class Study {
        private String name;
        private List<Method> methods;
        private OpenSource openSource;
        private List<String> languages;

        public Study(String name, List<Method> methods, OpenSource openSource, List<String> languages) {
            this.name = name;
            this.methods = methods;
            this.openSource = openSource;
            this.languages = languages;
        }

        public List<Method> getMethods() {
            return methods;
        }

        public OpenSource getOpenSource() {
            return openSource;
        }

        public List<String> getLanguages() {
            return languages;
        }

        @Override
        public String toString() {
            return "Study{" +
                    "name='" + name + '\'' +
                    ", method=" + methods +
                    ", openSource=" + openSource +
                    ", languages=" + languages +
                    '}';
        }

        public String tableRow(){
            StringBuilder methodBuilder = new StringBuilder();
            methodBuilder.append(methods.get(0).getString());
            if(methods.size() > 1){
                for(int i = 1; i < methods.size(); i++){
                    methodBuilder.append(" / ");
                    methodBuilder.append(methods.get(i).getString());
                }
            }

            String openSourceString = openSource.equals(OpenSource.YES) ? "Yes" : "No";

            StringBuilder languageBuilder = new StringBuilder();
            languageBuilder.append(languages.get(0));
            if(languages.size() > 1){
                for(int i = 1; i < languages.size(); i++){
                    languageBuilder.append(" / ");
                    languageBuilder.append(languages.get(i));
                }
            }

            return String.format("%s & \\ref{} & %s & %s & %s \\\\", name, methodBuilder.toString(), openSourceString, languageBuilder.toString());
        }
    }

    private static class MethodResult {
        private Method method;
        private long count;
        private int totalCount;

        public MethodResult(Method method, long count, int totalCount) {
            this.method = method;
            this.count = count;
            this.totalCount = totalCount;
        }

        public long getCount() {
            return count;
        }

        @Override
        public String toString() {
            String percentage = String.format("%.2f", 100.0 * count / totalCount);
            return "method=" + method +
                    ", count=" + count +
                    ", percentage=" + percentage;
        }

        public String toCSV(){
            return method + "," + count;
        }
    }

    private static class OpenSourceResult {
        private OpenSource openSource;
        private long count;
        private int totalCount;

        public OpenSourceResult(OpenSource openSource, long count, int totalCount) {
            this.openSource = openSource;
            this.count = count;
            this.totalCount = totalCount;
        }

        public long getCount() {
            return count;
        }

        @Override
        public String toString() {
            String percentage = String.format("%.2f", 100.0 * count / totalCount);
            return "openSource=" + openSource +
                    ", count=" + count +
                    ", percentage=" + percentage;
        }

        public String toCSV(){
            return openSource + "," + count;
        }
    }

    private static class LanguageResult {
        private String language;
        private long count;
        private int totalCount;

        public LanguageResult(String language, long count, int totalCount) {
            this.language = language;
            this.count = count;
            this.totalCount = totalCount;
        }

        public long getCount() {
            return count;
        }

        @Override
        public String toString() {
            String percentage = String.format("%.2f", 100.0 * count / totalCount);
            return "language=" + language +
                    ", count=" + count +
                    ", percentage=" + percentage;
        }

        public String toCSV(){
            return language + "," + count;
        }
    }
}
