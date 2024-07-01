package com.example.chummerly.model

class Deck(_name : String, _category : String, _theme : String) {
    private var name : String = ""
    private var category : String = ""
    private var theme : String = ""
    private var cardList : MutableList<Card> = mutableListOf()
    private var studyList : MutableList<Card> = mutableListOf<Card>() //this is temp..
    private var curCard : Int = 0


    init {
        name = _name
        category = _category
        theme = _theme
    }

    fun addCard(_cardId: Int, _question: String, _answer: String, _tag: String, _font: String) {
        //change this to var, if it causes problems when you edit a card
        val newCard = Card(_cardId, _question, _answer, _tag, _font)
        this.cardList.add(newCard)
    }

    fun removeCard(card: Card) {
        this.cardList.remove(card)
    }

    fun shuffleDeck() {
        this.cardList.shuffle()
    }

    fun deleteDeck() {
        this.cardList.clear()
    }

    fun getName() : String {
        return this.name
    }

    fun getCategory() : String {
        return this.category
    }

    fun getTheme() : String {
        return this.theme
    }

    fun editTheme(str: String) {
        this.theme = str
    }

    fun editName(str: String) {
        this.name = str
    }

    fun editCategory(str: String) {
        this.category = str
    }



    fun getCards() : List<Card> {
        this.studyList = this.cardList.filter { card -> !card.getCorrect() }.toMutableList()
        return this.studyList
    }

    fun getSpacedCards() : List<Card> {
        this.studyList = this.cardList.filter { card -> !card.getCorrect() }.toMutableList()
        //println("get_spaced")
        //this.studyList.addAll(this.cardList.filter { card -> card.mode == 1 })
         return this.studyList
    }

    fun getCardAt(index: Int) : Card {
        return this.cardList[index]
    }

    fun getNextCard() : Int {
        println("size:" +studyList.size)
       getCards()
        if(studyList.size-1 == 0){
            return -1
        } else {
            return 0
        }
        //var i : Int = 1
//        while(curCard +i < studyList.size){
//            if(!this.studyList[curCard + i].getCorrect()) {
//                println(studyList[curCard + i].getQuestion())
//                curCard = curCard +i
//                return curCard
//            }
//            //i++
//        }
        return -1

    }

    fun setSpacedAlgo(mode : Int, nextCard : Card){
        nextCard.mode = mode
    }

    fun getNextSpacedCard() : Int {
        getSpacedCards()
        var i : Int = 1
        while(i+curCard < studyList.size){
            if(this.studyList[curCard + i].mode == 1 || this.studyList[curCard + i].mode == 0) {
                curCard += i
                println("")
                return curCard
            }
            i++
        }
        //println("TERMINATE")
        return -1
//    }
//        if(studyList.size - 1 == 0) {
//            return -1
//        } else {
//            return 0
//        }
//        //var i : Int = 1
////        while(curCard +i < studyList.size){
////            if(!this.studyList[curCard + i].getCorrect()) {
////                println(studyList[curCard + i].getQuestion())
////                curCard = curCard +i
////                return curCard
////            }
////            //i++
////        }
    }



}
