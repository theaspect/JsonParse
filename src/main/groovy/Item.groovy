class Item {
    def name
    def url

    Item(name, url){
        this.name = name
        this.url = url
    }

    def String toString(){
        return "Name: " + this.name + " " + "Url: " + this.url +  " "
    }
}
