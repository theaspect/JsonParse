import groovy.json.JsonSlurper

class ParseJson {

    public static void main(String[] args) {

        def url = 'https://bower.herokuapp.com/packages/lookup/angular'.toURL().text

        if (url.charAt(0) != "[")
            url = '[' + url + ']'

        def jsonSlurper = new JsonSlurper().parseText(url)
        def parseData = jsonSlurper
        def urlList = new ArrayList<Item>()
        parseData.each {
            aUrl ->
                url = new Item(aUrl.name, aUrl.url)
                urlList.add(url)
        }

        urlList.each { aUrl -> println aUrl }

        URL aURL = new URL(urlList[0].url)

        def pathUrl = aURL.getPath().replaceAll("\\.git","")

        def fileName = 'bower.json'
        def gitHubUserContent = 'https://raw.githubusercontent.com'
        def tag = '/master'

        gitHubUserContent = gitHubUserContent + pathUrl + tag

        url = (gitHubUserContent + '/' + fileName).toURL().text

        parseData = new JsonSlurper().parseText(url)

        String mainUrl = parseData.main

        fileName = mainUrl.replaceAll("\\./","")

        url = gitHubUserContent + '/' + fileName
        def file = new File('main').newOutputStream()
        file << new URL(url).openStream()
        file.close()


        println(pathUrl)


    }

}