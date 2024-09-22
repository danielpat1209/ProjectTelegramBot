package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyNewBot extends TelegramLongPollingBot {
    private Map<String, Todo> userMap = new HashMap<>();
    private Map<String, Integer> userProgress = new HashMap<>();
    private Map<String, List<Question>> userQuestions = new HashMap<>();
    private Map<String, List<Integer>> userAnswers = new HashMap<>(); // Store user answers
    private boolean surveyActive = false;

    public MyNewBot() {
        this.userMap = new HashMap<>();
    }

    public void onUpdateReceived(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        String message = update.getMessage().getText();

        if (message.equals("/start") || message.equals("hi") || message.equals("היי")) {
            if (!userMap.containsKey(chatId)) {
                this.userMap.put(chatId, new Todo());
                sendToAllUsers("Welcome! New user joined. " +
                        "The size of the group is now: " + userMap.size());
            }

            if (surveyActive) {
                sendMessage(chatId, "A survey is already active. Please wait until it finishes.");
                return;
            }

            if (userMap.size() < 3) {
                sendMessage(chatId, "There are not enough members to create a survey. At least 3 members are required.");
                return;
            }

            sendMessage(chatId, "Creating a new survey. " +
                    "Do you want to send it immediately or after waiting?");
            sendMessage(chatId, "Type 'immediate' to send now, or 'waiting' to send after 1 minute.");
            surveyActive = true;

        } else if (message.startsWith("immediate")) {
            sendMessage(chatId, "You have 5 minutes to complete the survey!");
            createPoll();
            startTimer(chatId);
        } else if (message.startsWith("waiting")) {
            // מחשב את השעה הנוכחית והשעה המדויקת שהסקר יתחיל
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1); // דקה אחת המתנה
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String startTime = sdf.format(calendar.getTime());


            sendMessage(chatId, "The survey will start in one minute at " + startTime + ".");


            schedulePoll(1, chatId);
        } else {
            processAnswer(chatId, message);
        }
    }

    public void createPoll() {
        List<Question> questions = Arrays.asList(
                new Question("What is your favorite food?", Arrays.asList("1. Pizza", "2. Salad", "3. Steak", "4. Hamburger")),
                new Question("What is your favorite hobby?", Arrays.asList("1. Soccer", "2. Basketball", "3. Tennis", "4. Swimming")),
                new Question("What is your favorite animal?", Arrays.asList("1. Dog", "2. Cat", "3. Parrot"))
        );

        for (String chatId : userMap.keySet()) {
            userProgress.put(chatId, 0);
            userQuestions.put(chatId, new ArrayList<>(questions));
            userAnswers.put(chatId, new ArrayList<>());


            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String endTime = sdf.format(calendar.getTime());

            sendMessage(chatId, "You have 5 minutes to complete the survey! It will end at " + endTime + ".");
            sendMessage(chatId, "You will not be able to submit your answers after this time.");
            sendNextQuestion(chatId);
        }
    }

    public void sendNextQuestion(String chatId) {
        int currentQuestionIndex = userProgress.get(chatId);
        List<Question> questions = userQuestions.get(chatId);

        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            sendMessage(chatId, currentQuestion.getQuestionText());
            for (String answer : currentQuestion.getAnswers()) {
                sendMessage(chatId, answer);
            }
        } else {
            sendMessage(chatId, "Thank you for participating in the survey.");
            surveyActive = false;
            sendResults(chatId);
        }
    }

    public void processAnswer(String chatId, String answer) {
        int currentQuestionIndex = userProgress.get(chatId);
        List<Question> questions = userQuestions.get(chatId);

        if (currentQuestionIndex < questions.size() && !userAnswers.get(chatId).contains(currentQuestionIndex)) {
            int answerNumber = Integer.parseInt(answer.split("\\.")[0]) - 1; // Get answer number
            userAnswers.get(chatId).add(answerNumber); // Save user's answer
            userProgress.put(chatId, currentQuestionIndex + 1);
            sendNextQuestion(chatId);
        } else {
            sendMessage(chatId, "You have already answered this question.");
        }
    }

    public void sendToAllUsers(String message) {
        for (String chatId : userMap.keySet()) {
            sendMessage(chatId, message);
        }
    }

    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void schedulePoll(int delayMinutes, String chatId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                createPoll();
                startTimer(chatId);
                sendMessage(chatId, "The survey has started! You have 5 minutes to complete it.");
            }
        }, delayMinutes * 60 * 1000);
    }

    public void startTimer(String chatId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(chatId, "The time for the survey is over! Sending results...");
                surveyActive = false;
                sendResults(chatId);
            }
        }, 5 * 60 * 1000); // 5 minutes
    }

    public void sendResults(String chatId) {
        // Calculate and send results in percentages
        StringBuilder results = new StringBuilder("Survey results:\n");

        int totalUsers = userAnswers.size();
        int[] answerCounts = new int[4]; // Assuming up to 4 answers for each question

        for (List<Integer> answers : userAnswers.values()) {
            for (int i = 0; i < answers.size(); i++) {
                answerCounts[answers.get(i)]++;
            }
        }

        for (int i = 0; i < answerCounts.length; i++) {
            double percentage = (answerCounts[i] / (double) totalUsers) * 33.33;
            results.append("Answer ").append(i + 1).append(": ").append(String.format("%.2f", percentage)).append("%\n");
        }

        sendMessage(chatId, results.toString());
    }

    public String getBotUsername() {
        return "Daniel05Bot";
    }

    public String getBotToken() {
        return "7910627565:AAG4K7J7aQissOnUb6tYJHz6LnmmgRHFR4o";
    }
}



























