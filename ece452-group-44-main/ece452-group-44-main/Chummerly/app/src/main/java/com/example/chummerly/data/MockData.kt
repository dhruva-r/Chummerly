package com.example.chummerly.data

import com.example.chummerly.model.Deck

var counter : Boolean = true

var mockDecks = listOf(
    Deck(
        "CS 350 - Threads",
        "School Decks",
        "The basics of threads and how they work in computer systems. Includes core concepts such as thread management, interrupt handling, and preemption techniques."
    ),
    Deck(
        "CS 446 - Architecture",
        "School Decks",
        "Structure, Communication, Non-Functional requirements, Logical, physical, dynamic web architecture."
    ),
    Deck(
        "Indoor Care",
        "Plants",
        "Care tips for my indoor plants."
    )
)

fun getMockStudyDeck() : Deck {
    if(counter) {
        mockDecks[0].addCard(0, "What is the hardware + timesharing approach to implementing concurrent threads?", "Multiple threads running simultaneously with timesharing.", "tag", "font");
        mockDecks[0].addCard(1, "What is a thread?", "A sequence of instructions that provides a way for programmers to express concurrency in a program.", "tag", "font");
        mockDecks[0].addCard(2, "What happens on a context switch?", "The system switches which thread is running.", "tag", "font");
        mockDecks[0].addCard(3, "What is preemption?", "Forcing a running thread to stop running (so another thread can run).", "tag", "font");
        mockDecks[0].addCard(4, "What are the 3 ways to implement concurrent threads?", "Hardware, Timesharing, and Hardware + Timesharing", "tag", "font");
        mockDecks[0].addCard(5, "What are the different thread states", "New, Ready, Running, Waiting, Terminated", "tag", "font");
        mockDecks[0].addCard(6, "What is the hardware approach to implementing concurrent threads?", "P processors, C cores, M multithreading per core. Can execute PCM threads simultaneously.", "tag", "font");
        mockDecks[0].addCard(7, "What do all thread share?", "Address space and open files.", "tag", "font");
        mockDecks[0].addCard(8, "What do all threads have?", "CPU Registers and a stack (local variables).", "tag", "font");
        mockDecks[0].addCard(9, "What are interrupts?", "Events that occur during the execution of a program, signalling a device needs attention.", "tag", "font");
        mockDecks[0].addCard(10, "What causes interrupts?", "Hardware.", "tag", "font");
        mockDecks[0].addCard(11, "What are the reasons for using threads?", "Resource Utilization, Parallelism, Responsiveness, Priority, and Modularization.", "tag", "font");
        mockDecks[0].addCard(12, "How is preemption normally accomplished?", "Using interrupts.", "tag", "font");
        mockDecks[0].addCard(13, "What is the timesharing approach to implementing concurrent threads?", "Switching between multiple threads rapidly on the same hardware.", "tag", "font");
        mockDecks[0].addCard(14, "What are 4 ways to cause context switching?", "thread_yield, thread_exit, thread is preempted, and thread blocks following wait channel sleep.", "tag", "font");
        counter = false
    }
    return mockDecks[0]

}

