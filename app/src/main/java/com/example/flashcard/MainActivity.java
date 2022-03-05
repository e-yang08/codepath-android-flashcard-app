package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    boolean answerChoicesVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView flashcardQ = findViewById(R.id.flashcard_question_textview);
        TextView flashcardA = findViewById(R.id.flashcard_answer_textview);
        TextView choice1 = findViewById(R.id.answerChoice1_textview);
        TextView choice2 = findViewById(R.id.answerChoice2_textview);
        TextView choice3 = findViewById(R.id.answerChoice3_textview);
        ImageView toggle = findViewById(R.id.toggle_choices_visibility_imageview);

        // tap on question card to show the answer of flashcard
        flashcardQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardQ.setVisibility(View.INVISIBLE);
                flashcardA.setVisibility(View.VISIBLE);
            }
        });

        // tap on answer card to go back to the question of flashcard
        flashcardA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardA.setVisibility(View.INVISIBLE);
                flashcardQ.setVisibility(View.VISIBLE);
            }
        });

        // tap the wrong answer and check the correct answer
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice1.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                choice2.setBackgroundColor(getResources().getColor(R.color.green, null));
            }
        });

        // tap the correct answer
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice2.setBackgroundColor(getResources().getColor(R.color.green, null));
            }
        });

        // tap the wrong answer and check the correct answer
        choice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice2.setBackgroundColor(getResources().getColor(R.color.green, null));
                choice3.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
            }
        });

        // tap on toggle button to show or hide answer choices
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerChoicesVisible) {
                    choice1.setVisibility(View.INVISIBLE);
                    choice2.setVisibility(View.INVISIBLE);
                    choice3.setVisibility(View.INVISIBLE);
                    toggle.setImageResource(R.drawable.show_view);
                    answerChoicesVisible = false;
                } else {
                    choice1.setVisibility(View.VISIBLE);
                    choice2.setVisibility(View.VISIBLE);
                    choice3.setVisibility(View.VISIBLE);
                    toggle.setImageResource(R.drawable.hide_view);
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
                choice1.setVisibility(View.VISIBLE);
                choice2.setVisibility(View.VISIBLE);
                choice3.setVisibility(View.VISIBLE);
                toggle.setImageResource(R.drawable.hide_view);

                // set Question and answer cards' visibility
                flashcardA.setVisibility(View.INVISIBLE);
                flashcardQ.setVisibility(View.VISIBLE);

                //set multiple choices' color
                choice1.setBackgroundColor(getResources().getColor(R.color.white, null));
                choice2.setBackgroundColor(getResources().getColor(R.color.white, null));
                choice3.setBackgroundColor(getResources().getColor(R.color.white, null));
            }
        });
    }
}