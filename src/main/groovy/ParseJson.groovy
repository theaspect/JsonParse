import groovy.json.JsonSlurper

class ParseJson {

    def parseData
    def urlList
    String gitHubUserContent

    ParseJson(){

        this.parseData = null
        this.urlList = new ArrayList<Item>()
        this.gitHubUserContent = 'https://raw.githubusercontent.com'
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

    void conversionUrl(String version, String fileName) {

        URL aURL = new URL(urlList[0].url)

        gitHubUserContent = gitHubUserContent + aURL.getPath().replaceAll("\\.git", "") + "/" + version

        parseData = new JsonSlurper().parseText((gitHubUserContent + '/' + fileName).toURL().text)
    }

    void downloadMainLibrary() {

        String mainUrl = parseData.main

        def fileName = mainUrl.replaceAll("\\./", "")

        //#  save to file  #//

        inFile(gitHubUserContent + '/' + fileName, fileName)
    }

    void inFile(String url, String fileName) {

        def file = new File(fileName).newOutputStream()
        file << new URL(url).openStream()
        file.close()
    }

    public static void main(String[] args) {

        //args[0] = 'https://bower.herokuapp.com/packages/lookup/angular' - start url
        //args[2] = 'bower.json'  - package name
        //args[1] = 'master'  - version; none version - /master

        ParseJson parseJson = new ParseJson()

        //#  parse start url  #//

        parseJson.parseUrl(args[0].toURL().text)

        //#  conversion url  #//

        parseJson.conversionUrl(args[1], args[2])

        //#  download main lib  #//

        parseJson.downloadMainLibrary()


    }

}