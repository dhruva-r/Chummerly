package com.example.chummerly.model

class Card(_cardId: Int, _question: String, _answer: String, _tag: String, _font: String) {
    private var cardId: Int = 0
    private var question: String = ""
    private var answer: String = ""
    private var tag: String = ""
    private var font: String = ""
    private var face: Int = 0
    private var correctness: Int = 0
    public var mode: Int = 0

    // initializer block
    init {
        cardId = _cardId
        question = _question //variable names subject to change
        answer = _answer
        tag = _tag //interesting
        font = _font //not sure what the point of this is currently
        face = 0
    }

    fun editQuestion(newQuestion : String){
        question = newQuestion
    }

    fun editAnswer(newAnswer : String){
        answer = newAnswer
    }

    fun getCardId() : Int {
        return cardId
    }

    fun getAnswer() : String{
        return answer
    }

    fun getQuestion() : String{
        return question
    }

    fun flipCard() : String{
        if(face == 0){
            face = 1
            return getAnswer()
        } else {
            face = 0
            return getQuestion()
        }

    }

    fun getCorrect() : Boolean{
        return correctness != 0
    }

    fun setCorrect(){
        correctness = 1
    }

}
