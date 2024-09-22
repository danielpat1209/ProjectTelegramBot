package org.example;


import java.util.List;

public class Question {
        private String questionText; // טקסט השאלה
        private List<String> answers; // רשימת תשובות אפשריות



    public Question(String questionText, List<String> answers) {
            this.questionText = questionText;
            this.answers = answers;


    }

    public String getQuestionText() {
            return questionText;
        }

        public List<String> getAnswers() {
            return answers;


    }
}


