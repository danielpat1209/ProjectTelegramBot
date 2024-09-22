package org.example;

public class Todo {
        private String text; // טקסט התזכורת/המשימה
        private int seconds; // זמן משך המשימה (בשניות)

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }
    }


