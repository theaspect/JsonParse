import groovy.json.JsonSlurper

class ParseJson {

    public static void main(String[] args) {

        def url = 'https://bower.herokuapp.com/packages/search/angular'
        def file = new File('angular').newOutputStream()
        file << new URL(url).openStream()
        file.close()

        def jsonSlurper = new JsonSlurper()
        def reader = new BufferedReader(new FileReader("angular"))
        def parseData = jsonSlurper.parse(reader)
        def urlList = new ArrayList<Item>()
        parseData.each {
            aUrl ->
            url = new Item(aUrl.name, aUrl.url)
            urlList.add(url)
        }

        urlList.each {aUrl -> println aUrl}
    }

}
