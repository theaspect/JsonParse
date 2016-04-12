import groovy.json.JsonSlurper
import groovyjarjarcommonscli.*

class ParseJson {

    def parseData
    def urlList
    String gitHubUserContent
    String fileName

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

        parseData = new JsonSlurper().parseText((gitHubUserContent + '/' + fileName).toURL().text)
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
        } /*else {
            println("the file exists and it isn't empty")
        }*/


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
        url = commandLine.getOptionValue("url").toURL().text

        //#  parse start url  #//

        parseJson.parseUrl(url)

        //#  conversion url  #//

        parseJson.conversionUrl(version)

        //#  download main lib  #//

        parseJson.downloadMainLibrary(version)

        return 0

    }

    public static void main(String[] args) {
        System.exit(work(args))
    }

}