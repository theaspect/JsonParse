import groovy.json.JsonSlurper
import groovyjarjarcommonscli.*

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ParseJson {

    Logger logger = LogManager.getLogger(ParseJson.class);

    def parseData
    def urlList
    String gitHubUserContent
    String fileName
    private boolean fileNotFound = true
    private boolean fileExists = false

    public boolean isFileNotFound(){
        return fileNotFound
    }

    public boolean isFileExists(){
        return fileExists
    }

    ParseJson() {

        this.parseData = null
        this.urlList = new ArrayList<Item>()
        this.gitHubUserContent = "https://raw.githubusercontent.com"
        this.fileName = "bower.json"
    }

    void parseUrl(String url) {

        if (url.charAt(0) != "[")
            url = '[' + url + ']'


            parseData = new JsonSlurper().parseText(url)
            parseData.each {
                aUrl ->
                    def http = new Item(aUrl.name, aUrl.url)
                    urlList.add(http)
            }
    }

    void conversionUrl(String version) {

        URL aURL = new URL(urlList[0].url)

        gitHubUserContent = gitHubUserContent + aURL.getPath().replaceAll("\\.git", "") + "/" + version

        try {
            parseData = new JsonSlurper().parseText((gitHubUserContent + '/' + fileName).toURL().text)
        }catch (FileNotFoundException e){
            fileNotFound = false
            return
        }


    }

    void downloadMainLibrary(String version) {

        String mainUrl = parseData.main

        String fileName = mainUrl.replaceAll("\\./", "")

        String newFileName
        def parseTest = new ArrayList()
        parseTest = fileName.split("\\.")

        if (version != "master") {
            newFileName = parseTest[0] + "-" + version + "." + parseTest[1]
        } else {
            newFileName = fileName
        }


        if (!isFile(newFileName)) {
            //#  save to file  #//
            inFile(gitHubUserContent + '/' + fileName, newFileName)
        } else {
            fileExists = true
            return
        }


    }

    void inFile(String url, String fileName) {

        def file = new File(fileName).newOutputStream()
        file << new URL(url).openStream()
        file.close()
    }

    boolean isFile(String fileName) {

        boolean file = new File(fileName).exists()
        if (file) {
            File f = new File(fileName)
            if (f.length() == 0)
                file = false
        }
        return file

    }

    private static Option makeOptionWithArgument(String shortName, String description, boolean isRequired) {
        Option result = new Option(shortName, true, description);
        result.setArgs(1);
        result.setRequired(isRequired);

        return result;
    }

    static void printHelp(Options options) {
        final PrintWriter writer = new PrintWriter(System.out);
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(
                writer,
                80,
                "[program]",
                "Options:",
                options,
                3,
                5,
                "-- HELP --",
                true);

        writer.flush();
    }

    public static int work(String[] args) {

        Options options = new Options()
                .addOption(makeOptionWithArgument("version", "Version", false))
                .addOption(makeOptionWithArgument("url", "Url", true))

        CommandLine commandLine = null;
        try {
            commandLine = new GnuParser().parse(options, args)
        } catch (ParseException e) {
            printHelp(options);
            return 255;
        }

        ParseJson parseJson = new ParseJson()
        def version
        def url

        if (commandLine.getOptionValue("version")) {
            version = commandLine.getOptionValue("version")
        } else {
            version = 'master'
        }

        try {
            url = commandLine.getOptionValue("url").toURL().text
        }catch (FileNotFoundException e){
            parseJson.logger.warn("Package not found" + commandLine.getOptionValue("url"));
            return 1
        }
        //#  parse start url  #//

        parseJson.parseUrl(url)

        //#  conversion url  #//

        parseJson.conversionUrl(version)

        if(!parseJson.isFileNotFound()){
            parseJson.logger.warn("File not found or incorrect version");
            return 2
        }

        //#  download main lib  #//

        parseJson.downloadMainLibrary(version)
        if(parseJson.isFileExists()){
            parseJson.logger.warn("The file exists and it isn't empty");
            return 3
        }

        return 0
    }

    public static void main(String[] args) {
        System.exit(work(args))
    }

}