package com.example.flashcard;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {
    // global variables able to access variables in all methods of MainActivity

    boolean answerChoicesVisible = true; // true if multiple choices are visible
    boolean emptyState = false; // true if empty state is available
    boolean answerShown = false; // true if flashcard's question is hidden and answer is shown

    // a list of integer for choice index
    List<Integer> choiceList = Arrays.asList(1,2,3);

    // initialized the index of correct / wrong answer choices
    int wrongChoice1IndexInt = new Integer(1);
    int correctChoiceIndexInt = new Integer(2);
    int wrongChoice2IndexInt = new Integer(3);

    int addCARD_REQUEST_CODE = 100;
    int editCARD_REQUEST_CODE = 200;
    int firstCARD_REQUEST_CODE = 300;

    int nowDisplayedCardIndexInt = 0; // track the index of displayed card
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards; // holds a list of flashcards
    Flashcard editedCard;

    CountDownTimer countDownTimer; // count down timer for answering flashcard

    // create a function to (re)start timer
    private void startTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private KonfettiView konfettiView = null;
    private Shape.DrawableShape drawableShape = null;

    // create a function to generate a number in the range [minNumber, maxNumber] -- inclusive
    public int getRandomNumber(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

    // create a function to set multiple choices
    public int setMultipleChoice (TextView choice1, TextView choice2, TextView choice3,
                                  String correctAnswer, String wrongAnswer1,
                                  String wrongAnswer2){

        Collections.shuffle(choiceList);

        // assign answer choices
        correctChoiceIndexInt = choiceList.get(0);
        wrongChoice1IndexInt = choiceList.get(1);
        wrongChoice2IndexInt = choiceList.get(2);

        if (correctChoiceIndexInt == 1){
            choice1.setText("1. " + correctAnswer);
            if (wrongChoice1IndexInt == 2){
                choice2.setText("2. " + wrongAnswer1);
                choice3.setText("3. " + wrongAnswer2);
            } else {
                choice2.setText("2. " + wrongAnswer2);
                choice3.setText("3. " + wrongAnswer1);
            }
        } else if (correctChoiceIndexInt == 2){
            choice2.setText("2. " + correctAnswer);
            if (wrongChoice1IndexInt == 1){
                choice1.setText("1. " + wrongAnswer1);
                choice3.setText("3. " + wrongAnswer2);
            } else {
                choice1.setText("1. " + wrongAnswer2);
                choice3.setText("3. " + wrongAnswer1);
            }
        } else {
            choice3.setText("3. " + correctAnswer);
            if (wrongChoice1IndexInt == 1) {
                choice1.setText("1. " + wrongAnswer1);
                choice2.setText("2. " + wrongAnswer2);
            } else {
                choice1.setText("1. " + wrongAnswer2);
                choice2.setText("2. " + wrongAnswer1);
            }
        }
        return 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Flashcard");

//        System.out.println("before: correct"+correctChoiceIndexInt+"wrong1"+wrongChoice1IndexInt+"wrong2"+wrongChoice2IndexInt);

        TextView flashcardQTextView = findViewById(R.id.flashcard_question_textview);
        TextView flashcardATextView = findViewById(R.id.flashcard_answer_textview);
        TextView answerChoice1TextView = findViewById(R.id.answerChoice1_textview);
        TextView answerChoice2TextView = findViewById(R.id.answerChoice2_textview);
        TextView answerChoice3TextView = findViewById(R.id.answerChoice3_textview);
        TextView createFirstCardTextView = findViewById(R.id.create_flashcard_textview);
        TextView emptyStateTextView = findViewById(R.id.empty_state_textview);
        TextView countDownTimerTextView = findViewById(R.id.timer);

        ImageView toggleImageView = findViewById(R.id.toggle_choices_visibility_imageview);
        ImageView addQuestionImageView = findViewById(R.id.add_button_imageview);
        ImageView editQuestionImageView = findViewById(R.id.edit_button_imageview);
        ImageView nextQuestionImageView = findViewById(R.id.next_button_imageview);
        ImageView prevQuestionImageView = findViewById(R.id.prev_button_imageview);
        ImageView deleteQuestionImageView = findViewById(R.id.delete_button_imageview);
        ImageView emptyStateImageView = findViewById(R.id.empty_state_imageview);

        // after application is initialized, fetch updated flashcards
        flashcardDatabase = new FlashcardDatabase(getApplicationContext()); // or this
        allFlashcards = flashcardDatabase.getAllCards();

        // initialize the countdown timer - * start timerの前？後？or doesn't matter?
        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                countDownTimerTextView.setText("" + millisUntilFinished / 1000); // every 1 sec, time is updated
            }
            public void onFinish() {
                // show snack bar
                Snackbar.make(countDownTimerTextView,
                            "TIME'S UP" +("\uD83D\uDEA8"),
                            Snackbar.LENGTH_LONG)
                            .show();

                // remove the reveal animation and use card flip
                // get the center for the clipping circle
                int cx = flashcardATextView.getWidth() / 2;
                int cy = flashcardATextView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                // reveal gradually from the center
                Animator anim = ViewAnimationUtils.createCircularReveal(flashcardATextView, cx, cy, 0f, finalRadius);

                // hide the question and show the answer to prepare for playing the animation!
                flashcardQTextView.setVisibility(View.INVISIBLE);
                flashcardATextView.setVisibility(View.VISIBLE);
                anim.setDuration(500);
                anim.start();

                countDownTimer.cancel();
                countDownTimerTextView.setVisibility(View.INVISIBLE);

                answerShown = true;
            }
        };

        // prepare confetti
        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_heart);
        drawableShape = new Shape.DrawableShape(drawable, true);
        konfettiView = findViewById(R.id.konfettiView);

        // if there is no card
        if (allFlashcards.size() == 0) {
            emptyStateImageView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.VISIBLE);
            createFirstCardTextView.setVisibility(View.VISIBLE);

            flashcardQTextView.setVisibility(View.INVISIBLE);
            flashcardATextView.setVisibility(View.INVISIBLE);
            answerChoice1TextView.setVisibility(View.INVISIBLE);
            answerChoice2TextView.setVisibility(View.INVISIBLE);
            answerChoice3TextView.setVisibility(View.INVISIBLE);
            toggleImageView.setVisibility(View.INVISIBLE);
            addQuestionImageView.setVisibility(View.INVISIBLE);
            editQuestionImageView.setVisibility(View.INVISIBLE);
            nextQuestionImageView.setVisibility(View.INVISIBLE);
            prevQuestionImageView.setVisibility(View.INVISIBLE);
            deleteQuestionImageView.setVisibility(View.INVISIBLE);
            countDownTimerTextView.setVisibility(View.INVISIBLE);
            answerChoicesVisible = false;
            emptyState = true;

        //or only 1 flashcard, hide next / previous question
        } else if (allFlashcards.size() == 1) {
                nextQuestionImageView.setVisibility(View.INVISIBLE);
                prevQuestionImageView.setVisibility(View.INVISIBLE);
                emptyState = false;
            }


        // if flashcards exist, display the saved flashcard
        if (allFlashcards != null && allFlashcards.size() > 0) {

            //update the index of the first card to be displayed
            nowDisplayedCardIndexInt = getRandomNumber(0, allFlashcards.size() - 1);

            // get currently displayed flashcard's info
            Flashcard firstCard = allFlashcards.get(nowDisplayedCardIndexInt);

            // set the question and answer TextViews with data from the database
            flashcardQTextView.setText("Q. "+firstCard.getQuestion());
            flashcardATextView.setText("A. "+firstCard.getAnswer());

            // set multiple choices
            setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                    answerChoice3TextView, firstCard.getAnswer(),
                    firstCard.getWrongAnswer1(),firstCard.getWrongAnswer2());

            // start timer upon opening the app
            startTimer();
        }

        // tap on question card to show the answer of flashcard
        // with the flip animation
        flashcardQTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // remove the reveal animation and use card flip
//                // get the center for the clipping circle
//                int cx = flashcardATextView.getWidth() / 2;
//                int cy = flashcardATextView.getHeight() / 2;
//
//                // get the final radius for the clipping circle
//                float finalRadius = (float) Math.hypot(cx, cy);
//
//                // create the animator for this view (the start radius is zero)
//                // reveal gradually from the center
//                Animator anim = ViewAnimationUtils.createCircularReveal(flashcardATextView, cx, cy, 0f, finalRadius);
//
//                // hide the question and show the answer to prepare for playing the animation!
//                flashcardQTextView.setVisibility(View.INVISIBLE);
//                flashcardATextView.setVisibility(View.VISIBLE);
//                anim.setDuration(500);
//                anim.start();

                countDownTimer.cancel();
                countDownTimerTextView.setVisibility(View.INVISIBLE);

                answerShown = true;

//                flashcardQTextView.animate()
//                        .rotationY(90)
//                        .setDuration(200)
//                        .withEndAction(                         // chain two actions
//                                new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        answerShown = true;
//                                        flashcardATextView.setVisibility(View.VISIBLE);
//                                        flashcardQTextView.setVisibility(View.INVISIBLE);
//                                        // second quarter turn
//                                        flashcardATextView.setRotationY(-90);
//                                        flashcardATextView.animate()
//                                                .rotationY(0)
//                                                .setDuration(200)
//                                                .start();
//                                    }
//                                }
//                        ).start();

                // adjust the size
                flashcardQTextView.setCameraDistance(40000);
                flashcardATextView.setCameraDistance(40000);

                flashcardQTextView.animate()
                        .rotationY(90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        flashcardQTextView.setVisibility(View.INVISIBLE);
                                        flashcardATextView.setVisibility(View.VISIBLE);
                                        flashcardATextView.setRotationY(-90);
                                        flashcardQTextView.setRotationY(0);
                                    }
                                }
                        )
                        .start();

                flashcardATextView.animate()
                        .rotationY(0)
                        .setDuration(200)
                        .setStartDelay(200)
                        .start();


            }
        });

        // tap on answer card to go back to the question of flashcard
        flashcardATextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answerShown = false;
                startTimer();
                countDownTimerTextView.setVisibility(View.VISIBLE);

                // adjust the size
                flashcardQTextView.setCameraDistance(40000);
                flashcardATextView.setCameraDistance(40000);

                flashcardATextView.animate()
                        .rotationY(-90)
                        .setDuration(200)
                        .withEndAction(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        flashcardATextView.setVisibility(View.INVISIBLE);
                                        flashcardQTextView.setVisibility(View.VISIBLE);
                                        flashcardQTextView.setRotationY(90);
                                        flashcardATextView.setRotationY(0);
                                    }
                                }
                                )
                        .start();

                flashcardQTextView.animate()
                        .rotationY(0)
                        .setDuration(200)
                        .setStartDelay(200)
                        .start();
            }
        });


        // light up the answer in green when user tap the correct answer
        // but in red otherwise -- the correct answer is shown in green background
        // the other wrong answer is remained to be white, so that when user tap multiple times,
        // the light is always set
        answerChoice1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctChoiceIndexInt == 1){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    // confetti
                    parade();
                } else if (correctChoiceIndexInt == 2){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                } else {
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
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
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                } else if (correctChoiceIndexInt == 2){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    parade();
                } else {
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
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
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                } else if (correctChoiceIndexInt == 2){
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.sheer_red, null));
                } else {
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.green, null));
                    parade();
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
                if (!emptyState){

                    //set multiple choices' visibility
                    answerChoicesVisible = true;
                    answerChoice1TextView.setVisibility(View.VISIBLE);
                    answerChoice2TextView.setVisibility(View.VISIBLE);
                    answerChoice3TextView.setVisibility(View.VISIBLE);
                    toggleImageView.setImageResource(R.drawable.hide_view);

//                    // set Question and answer cards' visibility
//                    flashcardATextView.setVisibility(View.INVISIBLE);
//                    flashcardQTextView.setVisibility(View.VISIBLE);

                    //set multiple choices' color
                    answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));

                    // start timer
                    startTimer();
                }
            }
        });

        // tap plus button to move to a card create page
        addQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivityForResult(intent, addCARD_REQUEST_CODE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // from empty state to move to a card create page
        createFirstCardTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivityForResult(intent, firstCARD_REQUEST_CODE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // tap edit button to move to edit page
        editQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find the currently shown card on the deck now
                editedCard = allFlashcards.get(nowDisplayedCardIndexInt);

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
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // tap next button to move to a different flashcard
        nextQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // don't try to go to next card if you have no cards to begin with or there's single card
                if (allFlashcards.size() == 1){
                    return;
                }

                // initialize currently displayed card's index
                int cardToDisplayIndex = getRandomNumber(0, allFlashcards.size() - 1);

                // find another card if the next card is the same as the current displayed one
                while (cardToDisplayIndex == nowDisplayedCardIndexInt) {
                    cardToDisplayIndex = getRandomNumber(0, allFlashcards.size() - 1);
                }

                nowDisplayedCardIndexInt = cardToDisplayIndex;

                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);


                if (answerShown) {
                    flashcardATextView.startAnimation(leftOutAnim);
                }
                else{
                    flashcardQTextView.startAnimation(leftOutAnim);
                }

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        Flashcard currentCard = allFlashcards.get(nowDisplayedCardIndexInt);

                        // set the question and answer TextViews with data from the database
                        flashcardQTextView.setText("Q. "+currentCard.getQuestion());
                        flashcardATextView.setText("A. "+currentCard.getAnswer());

                        if (answerShown) {
                            // Switch back to showing question
                            flashcardATextView.setVisibility(View.INVISIBLE);
                            flashcardQTextView.animate().rotationY(0).setDuration(0).start();
                            flashcardQTextView.setVisibility(View.VISIBLE);
                            countDownTimerTextView.setVisibility(View.VISIBLE);
                            answerShown = false;
                        }

                        // set multiple choices
                        setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                                answerChoice3TextView, currentCard.getAnswer(),
                                currentCard.getWrongAnswer1(),currentCard.getWrongAnswer2());

                        // start the next animation
                        flashcardQTextView.startAnimation(rightInAnim);

                        // start timer
                        startTimer();

                        // reset the background color of multiple choices
                        answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                        answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                        answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });


//                // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
//                if(nowDisplayedCardIndexInt >= allFlashcards.size()) {
//                    Snackbar.make(v,
//                            "It is the last flash card. ",
//                            Snackbar.LENGTH_SHORT)
//                            .show();
//                    nowDisplayedCardIndexInt = 0; // reset index to go back to the beginning
//                };

            }});


        // tap previous button to move to a different flashcard
        prevQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // don't try to go to previous card if you have no cards to begin with or there's single card
                if (allFlashcards.size() == 1){
                    return;
                }

                // initialize currently displayed card's index
                int cardToDisplayIndex2 = getRandomNumber(0, allFlashcards.size() - 1);

                // find another card if the next card is the same as the current displayed one
                while (cardToDisplayIndex2 == nowDisplayedCardIndexInt) {
                    cardToDisplayIndex2 = getRandomNumber(0, allFlashcards.size() - 1);
                }

                nowDisplayedCardIndexInt = cardToDisplayIndex2;

                final Animation leftInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_in);
                final Animation rightOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_out);

                if (answerShown) {
                    flashcardATextView.startAnimation(rightOutAnim);
                }
                else{
                    flashcardQTextView.startAnimation(rightOutAnim);
                }

                rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        Flashcard currentCard = allFlashcards.get(nowDisplayedCardIndexInt);

                        // set the question and answer TextViews with data from the database
                        flashcardQTextView.setText("Q. "+currentCard.getQuestion());
                        flashcardATextView.setText("A. "+currentCard.getAnswer());

                        if (answerShown) {
                            flashcardATextView.setVisibility(View.INVISIBLE);
                            flashcardQTextView.setVisibility(View.VISIBLE);
                            flashcardQTextView.setRotationY(0);
                            answerShown = false;
                        }

                        // set multiple choices
                        setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                                answerChoice3TextView, currentCard.getAnswer(),
                                currentCard.getWrongAnswer1(),currentCard.getWrongAnswer2());

                        // start the moving-previous-card animation
                        flashcardQTextView.startAnimation(leftInAnim);

                        // start timer
                        startTimer();


                        // reset the background color of multiple choices
                        answerChoice1TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                        answerChoice2TextView.setBackgroundColor(getResources().getColor(R.color.white, null));
                        answerChoice3TextView.setBackgroundColor(getResources().getColor(R.color.white, null));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });
            }});

        // delete question
        deleteQuestionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the first 3 letters (i.e., "Q. ") to detect the card in database
                flashcardDatabase.deleteCard(flashcardQTextView.getText().toString().substring(3));
                allFlashcards = flashcardDatabase.getAllCards();

                //if there is no card after deletion, show empty state
                if (allFlashcards.isEmpty()){
                    // make everything invisible
                    flashcardATextView.setVisibility(View.INVISIBLE);
                    flashcardQTextView.setVisibility(View.INVISIBLE);
                    answerChoice1TextView.setVisibility(View.INVISIBLE);
                    answerChoice2TextView.setVisibility(View.INVISIBLE);
                    answerChoice3TextView.setVisibility(View.INVISIBLE);
                    addQuestionImageView.setVisibility(View.INVISIBLE);
                    editQuestionImageView.setVisibility(View.INVISIBLE);
                    toggleImageView.setVisibility(View.INVISIBLE);
                    nextQuestionImageView.setVisibility(View.INVISIBLE);
                    prevQuestionImageView.setVisibility(View.INVISIBLE);
                    deleteQuestionImageView.setVisibility(View.INVISIBLE);
                    countDownTimerTextView.setVisibility(View.INVISIBLE);
                    answerChoicesVisible = false;

                    // show empty state
                    emptyState = true;
                    emptyStateImageView.setVisibility(View.VISIBLE);
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    createFirstCardTextView.setVisibility(View.VISIBLE);
                } else {

                    // randomly display a new card in the deck
                    nowDisplayedCardIndexInt = getRandomNumber(0, allFlashcards.size() - 1);

//                    // display previous card in the deck
//                    nowDisplayedCardIndexInt --;
//
//                    // if the deleted card was the first card, show the last card instead
//                    if (nowDisplayedCardIndexInt == -1) {
//                        nowDisplayedCardIndexInt = allFlashcards.size() - 1;
//                    }

                    // display the card
                    Flashcard flashcard = allFlashcards.get(nowDisplayedCardIndexInt);
                    flashcardQTextView.setText("Q. "+flashcard.getQuestion());
                    flashcardATextView.setText("A. "+flashcard.getAnswer());
                    // set multiple choices
                    // based on the shuffled result, set choices in corresponding textview
                    setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                            answerChoice3TextView, flashcard.getAnswer(),
                            flashcard.getWrongAnswer1(),flashcard.getWrongAnswer2());

                    // when there is only one remaining flashcard, make the buttons invisible
                    if (allFlashcards.size() == 1) {
                        nextQuestionImageView.setVisibility(View.INVISIBLE);
                        prevQuestionImageView.setVisibility(View.INVISIBLE);
                    }

                    // start timer
                    startTimer();
            }}
        });

    }

    // from https://github.com/DanielMartinus/Konfetti
    public void parade() {
        EmitterConfig emitterConfig = new Emitter(3, TimeUnit.SECONDS).perSecond(40);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .angle(Angle.RIGHT - 45)
                        .spread(Spread.SMALL)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(0.0, 0.5))
                        .build(),
                new PartyFactory(emitterConfig)
                        .angle(Angle.LEFT + 45)
                        .spread(Spread.SMALL)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(1.0, 0.5))
                        .build()
        );
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
        TextView countDownTimerTextView= findViewById(R.id.timer);

        ImageView toggleImageView = findViewById(R.id.toggle_choices_visibility_imageview);
        ImageView addQuestionImageView = findViewById(R.id.add_button_imageview);
        ImageView editQuestionImageView = findViewById(R.id.edit_button_imageview);
        ImageView deleteQuestionImageView = findViewById(R.id.delete_button_imageview);
        ImageView nextQuestionImageView = findViewById(R.id.next_button_imageview);
        ImageView prevQuestionImageView = findViewById(R.id.prev_button_imageview);

        TextView createFirstCardTextView = findViewById(R.id.create_flashcard_textview);
        TextView emptyStateTextView = findViewById(R.id.empty_state_textview);
        ImageView emptyStateImageView = findViewById(R.id.empty_state_imageview);

//        System.out.println("post: correct"+correctChoiceIndexInt+"wrong1"+wrongChoice1IndexInt+"wrong2"+wrongChoice2IndexInt);


        if (data != null) {
            // key has to match with the one from addcardactivity
            String questionString = data.getExtras().getString("question");
            String correctAnswerString = data.getExtras().getString("correctAnswer");
            String wrongAnswer1String = data.getExtras().getString("wrongAnswer1");
            String wrongAnswer2String = data.getExtras().getString("wrongAnswer2");

            // set the question and answer passed from main activity
            flashcardQTextView.setText("Q. " + questionString);
            flashcardATextView.setText("A. " + correctAnswerString);

            // when resultCode == RESULT_OK
            if (requestCode == addCARD_REQUEST_CODE) {

                // set multiple choices
                // based on the shuffled result, set choices in corresponding textview
                setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                        answerChoice3TextView, correctAnswerString,
                        wrongAnswer1String,wrongAnswer2String);

                // show a snackbar message
                Snackbar.make(flashcardQTextView,
                        "Card successfully created!",
                        Snackbar.LENGTH_SHORT)
                        .show();

                // insert card to the database
                flashcardDatabase.insertCard(new Flashcard(questionString, correctAnswerString, wrongAnswer1String, wrongAnswer2String));

                // update database
                allFlashcards = flashcardDatabase.getAllCards(); // fetch data from updated flashcards

                // update the card index to the currently added one (last one)
                nowDisplayedCardIndexInt = allFlashcards.size() - 1;

                if (allFlashcards.size() > 1) {
                    nextQuestionImageView.setVisibility(View.VISIBLE);
                    prevQuestionImageView.setVisibility(View.VISIBLE);
                }

                // start timer
                startTimer();

            } else if (requestCode == editCARD_REQUEST_CODE){
                // update card
                editedCard.setQuestion(questionString);
                editedCard.setAnswer(correctAnswerString);
                editedCard.setWrongAnswer1(wrongAnswer1String);
                editedCard.setWrongAnswer2(wrongAnswer2String);

                // randomized the multiple choices
                setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                        answerChoice3TextView, correctAnswerString,
                        wrongAnswer1String,wrongAnswer2String);

                // update database
                flashcardDatabase.updateCard(editedCard);
                allFlashcards = flashcardDatabase.getAllCards();

                // show a snackbar message
                Snackbar.make(flashcardQTextView,
                        "Card successfully edited!",
                        Snackbar.LENGTH_SHORT)
                        .show();

                // start timer
                startTimer();

            } else if (requestCode == firstCARD_REQUEST_CODE){
                emptyStateImageView.setVisibility(View.INVISIBLE);
                emptyStateTextView.setVisibility(View.INVISIBLE);
                createFirstCardTextView.setVisibility(View.INVISIBLE);
                emptyState = false;

                flashcardQTextView.setVisibility(View.VISIBLE);
                flashcardATextView.setVisibility(View.VISIBLE);
                answerChoice1TextView.setVisibility(View.VISIBLE);
                answerChoice2TextView.setVisibility(View.VISIBLE);
                answerChoice3TextView.setVisibility(View.VISIBLE);
                toggleImageView.setVisibility(View.VISIBLE);
                addQuestionImageView.setVisibility(View.VISIBLE);
                editQuestionImageView.setVisibility(View.VISIBLE);
                deleteQuestionImageView.setVisibility(View.VISIBLE);
                countDownTimerTextView.setVisibility(View.VISIBLE);

                // show a snackbar message
                Snackbar.make(flashcardQTextView,
                        "Foolay"+("\ud83d\ude4c")+ "Your 1st card is successfully created!",
                        Snackbar.LENGTH_SHORT)
                        .show();

                flashcardDatabase.insertCard(new Flashcard(questionString, correctAnswerString, wrongAnswer1String, wrongAnswer2String));
                allFlashcards = flashcardDatabase.getAllCards(); // fetch data from updated flashcards

                // update the card index to the currently added one (last one)
                nowDisplayedCardIndexInt = allFlashcards.size() - 1;

                // set multiple choices
                // based on the shuffled result, set choices in corresponding textview
                setMultipleChoice (answerChoice1TextView, answerChoice2TextView,
                        answerChoice3TextView, correctAnswerString,
                        wrongAnswer1String,wrongAnswer2String);

                // start timer
                startTimer();
            }
        }
    }
}