package com.example.flashcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    boolean answerChoicesVisible = true;

    // initialized the index of correct / wrong answer choices

    int wrongChoice1IndexInt = new Integer(1);
    int correctChoiceIndexInt = new Integer(2);
    int wrongChoice2IndexInt = new Integer(3);
    int addCARD_REQUEST_CODE = 100;
    int editCARD_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        System.out.println("before: correct"+correctChoiceIndexInt+"wrong1"+wrongChoice1IndexInt+"wrong2"+wrongChoice2IndexInt);

        TextView flashcardQTextView = findViewById(R.id.flashcard_question_textview);
        TextView flashcardATextView = findViewById(R.id.flashcard_answer_textview);
        TextView answerChoice1TextView = findViewById(R.id.answerChoice1_textview);
        TextView answerChoice2TextView = findViewById(R.id.answerChoice2_textview);
        TextView answerChoice3TextView = findViewById(R.id.answerChoice3_textview);

        ImageView toggleImageView = findViewById(R.id.toggle_choices_visibility_imageview);

        // tap on question card to show the answer of flashcard
        flashcardQTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardQTextView.setVisibility(View.INVISIBLE);
                flashcardATextView.setVisibility(View.VISIBLE);
            }
        });

        // tap on answer card to go back to the question of flashcard
        flashcardATextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardATextView.setVisibility(View.INVISIBLE);
                flashcardQTextView.setVisibility(View.VISIBLE);
            }
        });


        // light up the answer in green when user tap the correct answer
        // but in red otherwise -- the correct answer is shown in green background
        answerChoice1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctChoiceIndexInt == 1){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                } else if (correctChoiceIndexInt == 2){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                } else {
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                }
            }
        });

        answerChoice2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctChoiceIndexInt == 1){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                } else if (correctChoiceIndexInt == 2){
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                } else {
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                }
            }
        });

        answerChoice3TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctChoiceIndexInt == 1){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                } else if (correctChoiceIndexInt == 2){
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                } else {
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                }
            }
        });

        // tap on toggle button to show or hide answer choices
        toggleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerChoicesVisible) {
                    answerChoice1TextView.setVisibility(View.INVISIBLE);
                    answerChoice2TextView.setVisibility(View.INVISIBLE);
                    answerChoice3TextView.setVisibility(View.INVISIBLE);
                    toggleImageView.setImageResource(R.drawable.show_view);
                    answerChoicesVisible = false;
                } else {
                    answerChoice1TextView.setVisibility(View.VISIBLE);
                    answerChoice2TextView.setVisibility(View.VISIBLE);
                    answerChoice3TextView.setVisibility(View.VISIBLE);
                    toggleImageView.setImageResource(R.drawable.hide_view);
                    answerChoicesVisible = true;
                }
            }
        });

        //tap on the background to reset all views to default settings
        findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set multiple choices' visibility
                answerChoicesVisible = true;
                answerChoice1TextView.setVisibility(View.VISIBLE);
                answerChoice2TextView.setVisibility(View.VISIBLE);
                answerChoice3TextView.setVisibility(View.VISIBLE);
                toggleImageView.setImageResource(R.drawable.hide_view);

                // set Question and answer cards' visibility
                flashcardATextView.setVisibility(View.INVISIBLE);
                flashcardQTextView.setVisibility(View.VISIBLE);

                //set multiple choices' color
                answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
            }
        });

        ImageView addQuestionImageView = findViewById(R.id.add_button_imageview);
//        ImageView nextQuestionImageView = findViewById(R.id.next_button_imageview);
        ImageView editQuestionImageView = findViewById(R.id.edit_button_imageview);

        // tap plus button to move to a new page
        addQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivityForResult(intent, addCARD_REQUEST_CODE);
            }
        });

        // tap edit button to move to edit page
        editQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                intent.putExtra("question",flashcardQTextView.getText().toString().substring(3));
                intent.putExtra("correctAnswer",flashcardATextView.getText().toString().substring(3));

                // based on answer choice's index, receive different input
                if (correctChoiceIndexInt == 1) {
                    intent.putExtra("wrongAnswer1", answerChoice2TextView.getText().toString().substring(3));
                    intent.putExtra("wrongAnswer2", answerChoice3TextView.getText().toString().substring(3));
                } else if (correctChoiceIndexInt == 2){
                    intent.putExtra("wrongAnswer1",answerChoice1TextView.getText().toString().substring(3));
                    intent.putExtra("wrongAnswer2",answerChoice3TextView.getText().toString().substring(3));
                } else {
                    intent.putExtra("wrongAnswer1",answerChoice1TextView.getText().toString().substring(3));
                    intent.putExtra("wrongAnswer2",answerChoice2TextView.getText().toString().substring(3));
                }
                startActivityForResult(intent, editCARD_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        TextView flashcardQTextView = findViewById(R.id.flashcard_question_textview);
        TextView flashcardATextView = findViewById(R.id.flashcard_answer_textview);
        TextView answerChoice1TextView = findViewById(R.id.answerChoice1_textview);
        TextView answerChoice2TextView = findViewById(R.id.answerChoice2_textview);
        TextView answerChoice3TextView = findViewById(R.id.answerChoice3_textview);

        // randomly shuffle a integer from [1,2,3]
        List<Integer> choiceList = Arrays.asList(1,2,3);
        Collections.shuffle(choiceList);

        // assign answer choices
        correctChoiceIndexInt = choiceList.get(0);
        wrongChoice1IndexInt = choiceList.get(1);
        wrongChoice2IndexInt = choiceList.get(2);

//        System.out.println("post: correct"+correctChoiceIndexInt+"wrong1"+wrongChoice1IndexInt+"wrong2"+wrongChoice2IndexInt);


        if (data != null) {
            // key has to match with the one from addcardactivity
            String questionString = data.getExtras().getString("question");
            String correctAnswerString = data.getExtras().getString("correctAnswer");
            String wrongAnswer1String = data.getExtras().getString("wrongAnswer1");
            String wrongAnswer2String = data.getExtras().getString("wrongAnswer2");

            flashcardQTextView.setText("Q. " + questionString);
            flashcardATextView.setText("A. " + correctAnswerString);

            // based on the shuffled result, set choices in corresponding textview
            if (correctChoiceIndexInt == 1){
                answerChoice1TextView.setText("1. " + correctAnswerString);
                if (wrongChoice1IndexInt == 2){
                    answerChoice2TextView.setText("2. " + wrongAnswer1String);
                    answerChoice3TextView.setText("3. " + wrongAnswer2String);
                } else {
                    answerChoice2TextView.setText("2. " + wrongAnswer2String);
                    answerChoice3TextView.setText("3. " + wrongAnswer1String);
                }
            } else if (correctChoiceIndexInt == 2){
                answerChoice2TextView.setText("2. " + correctAnswerString);
                if (wrongChoice1IndexInt == 1){
                    answerChoice1TextView.setText("1. " + wrongAnswer1String);
                    answerChoice3TextView.setText("3. " + wrongAnswer2String);
                } else {
                    answerChoice1TextView.setText("1. " + wrongAnswer2String);
                    answerChoice3TextView.setText("3. " + wrongAnswer1String);
                }
            } else {
                answerChoice3TextView.setText("3. " + correctAnswerString);
                if (wrongChoice1IndexInt == 1) {
                    answerChoice1TextView.setText("1. " + wrongAnswer1String);
                    answerChoice2TextView.setText("2. " + wrongAnswer2String);
                } else {
                    answerChoice1TextView.setText("1. " + wrongAnswer2String);
                    answerChoice2TextView.setText("2. " + wrongAnswer1String);
                }
            }
        }

        // when resultCode == RESULT_OK
        // set the question and answer passed from main activity
        if (requestCode == addCARD_REQUEST_CODE) {
//            flashcardDatabase.insertCard(new Flashcard(questionString, answerString));
//            allFlashcards = flashcardDatabase.getAllCards(); // fetch data from updated flashcards

            // show a snackbar message
            Snackbar.make(flashcardQTextView,
                    "Card successfully created!",
                    Snackbar.LENGTH_SHORT)
                    .show();

        } else if (requestCode == editCARD_REQUEST_CODE){
            // show a snackbar message
            Snackbar.make(flashcardQTextView,
                    "Card successfully! edited",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}