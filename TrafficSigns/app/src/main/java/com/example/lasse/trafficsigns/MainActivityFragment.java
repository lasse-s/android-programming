package com.example.lasse.trafficsigns;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivityFragment extends Fragment {

    // String virheilmoituksille
    private static final String TAG = "SignQuiz Activity";

    private static final int SIGNS_IN_QUIZ = 10;

    private List<String> fileNameList; // lipputiedostojen nimet
    private List<String> quizSignsList; // visailun maat
    private String correctAnswer; // oikea vastaus lipulle
    private int totalGuesses; // arvausten määrä
    static int staticTotalGuesses; // LISÄTTY apumuuttuja lisätyn sisäluokan vaatimuksesta......
    private int correctAnswers; // oikeiden vastausten määrä
    private int guessRows; // näytettävien lippurivien määrä
    private SecureRandom random; // visailun satunnaistaminen
    private Handler handler; // viive lippujen lataamisessa
    private Animation shakeAnimation; // oikean lipun animointi

    private LinearLayout quizLinearLayout; // asettelu visalle
    private TextView questionNumberTextView; // kysymyksen numero
    private ImageView signImageView; // näyttää lipun
    private LinearLayout[] guessLinearLayouts; // rivit vastauspainikkeille
    private TextView answerTextView; // näyttää oikean vastauksen

    /**
     * Muokkaa Fragmenttia kun näkymä luodaan
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        quizSignsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        // ladataan ravistus animaatio, jota käytetään oikeille vastauksille
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // toistetaan kolmesti

        // liitetään muuttujat käyttöliittymän komponentteihin
        quizLinearLayout = (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = (TextView) view.findViewById(R.id.questionNumberTextView);
        signImageView = (ImageView) view.findViewById(R.id.signImageview);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = (LinearLayout) view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = (LinearLayout) view.findViewById(R.id.row4LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

        // asetetaan painikkeille kuuntelijat
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        // asetetaan kysymysten määrän teksti
        questionNumberTextView.setText(getString(R.string.question_number, 1, SIGNS_IN_QUIZ));

        return view; // palautetaan näkymä näytettäväksi
    }

    /**
     * Päivitetään guessRows vastaamaan SharedPreferences arvoa
     * @param sharedPreferences
     */
    public void updateGuessRows(SharedPreferences sharedPreferences) {
        // luetaan näytettävien painikkeiden määrä
        String choices =
                sharedPreferences.getString(MainActivity.CHOICES, "4");
        guessRows = Integer.valueOf(choices) / 2;

        // piilotetaan kaikki lippujen näkymät
        for (LinearLayout layout : guessLinearLayouts)
            layout.setVisibility(View.GONE);

        // näytetään sopiva määrä painikkeiden näkymiä
        for (int row = 0; row < guessRows; row++)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    /**
     * valmistellaan ja käynnistetään visailu
     */
    public void resetQuiz() {
        // käytetään Assetmanageria kuvatiedostojen nimien hakemiseen
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); // tyhjennetään lista

        try {
            String[] paths = assets.list("images");

            for (String path : paths)
                if(path.endsWith(".jpg")) {
                    fileNameList.add(path.replace(".jpg", ""));
                }
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        quizSignsList.clear();

        int signCounter = 1;
        int numberOfSigns = fileNameList.size();

        // lisätään SIGNS_IN_QUIZ määrä satunnaisia lipputiedostoja lippuihin
        while (signCounter <= SIGNS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfSigns);

            // haetaan satunnaisen tiedoston nimi
            String filename = fileNameList.get(randomIndex);

            // jos alue sallittu ja lippua ei ole jo valittu
            if (!quizSignsList.contains(filename)) {
                quizSignsList.add(filename); // lisätään tiedosto
                ++signCounter;
            }
        }

        loadNextSign(); // käynnistetään lataamalla ensimmäinen lippu, seuraavassa määritellään tämä
    }


    /**
     * kun oikea lippu arvattu, ladataan seuraava lippu.
     */
    private void loadNextSign() {
        // luetaan tiedoston nimi ja lippu, ja poistetaan listalta
        String nextImage = quizSignsList.remove(0);
        correctAnswer = nextImage; // päivitetään oikea vastaus
        answerTextView.setText(""); // tyhjennetään vastaus

        // näytetään kysymyksen numero
        questionNumberTextView.setText(getString(
                R.string.question_number, (correctAnswers + 1), SIGNS_IN_QUIZ));

        // ladataan kuva Asset managerin avulla
        AssetManager assets = getActivity().getAssets();

        // määritellään lukemiseen inputStream ja koetetaan lukea sitä
        try (InputStream stream =
                     assets.open("images/" + nextImage + ".jpg")) {
            // ladataan asset ja näytetään signImageView:ssa
            Drawable sign = Drawable.createFromStream(stream, nextImage);
            signImageView.setImageDrawable(sign);

            animate(false); // animoidaan näytölle, määritellään myöhemmin
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading" + nextImage, exception);
        }

        Collections.shuffle(fileNameList); // sekoitetaan nimilista

        // laitetaan oikean vaihtoehto listan loppuun
        int correct = fileNameList.indexOf((correctAnswer));
        fileNameList.add(fileNameList.remove(correct));

        // lisätään 2, 4, 6, tai 8 arvauspainiketta riippuen guessRow:sta
        for (int row = 0; row < guessRows; row++) {
            // sijoitetaan painikkeet nykyiselle riville
            for (int column = 0;
                 column < guessLinearLayouts[row].getChildCount();
                 column++) {
                // haetaan viittaus muokattavaan painikkeeseen
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                // haetaan maan nimi ja asetetaan se Buttonin tekstiksi
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getSignName(filename)); // määritellään jatkoista
            }
        }

        // vaihdetaan satunnainen painike oikeaan
        int row = random.nextInt(guessRows); // satunnainen rivi
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        String signName = getSignName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(signName);

    }

    /**
     * metodi liittää lipun tiedoston nimeen ja palauttaa maan nimen
     * @param name
     * @return
     */
    private String getSignName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    /**
     * animoi koko quizLinearLayout:n näytölle tai sieltä pois
     * @param animateOut
     */
    private void animate(boolean animateOut) {
        // estetään ensimmäisen lipun animointi
        if (correctAnswers == 0)
            return;

        // lasketaan keskipisteen koordinaatit
        int centerX = (quizLinearLayout.getLeft() +
                quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() +
                quizLinearLayout.getBottom()) / 2;

        // lasketaan animaation säde
        int radius = Math.max(quizLinearLayout.getWidth(),
                quizLinearLayout.getHeight());

        Animator animator;

        // jos animoidaan ulos eikä sisään
        if (animateOut) {
            // luodaan kehämäinen animaatio
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        // kutsutaan kun animaatio päättyy
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadNextSign();
                        }
                    }
            );
        }
        else { // jos animaatio tulee sisään
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500); // kesto 500 ms
        animator.start(); // käynnistetään animaatio
    }

    // kutsutaan kun arvaus painikkeeseen kosketaan
    private OnClickListener guessButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getSignName(correctAnswer);
            ++totalGuesses; // kasvatetaan arvausten määrää

            if (guess.equals(answer)) { // arvaus oikein
                ++correctAnswers; // kasvatetaan oikeita vastauksia

                // näytetään oikea vastaus vihreällä tekstillä
                answerTextView.setText((answer + "!").toUpperCase());
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                getContext().getTheme()));

                disableButtons(); // painikkeet ei enää toimi, määritellään jatkossa


                //KORJAUS TÄHÄN VÄLIIN, JOTTA TULOKSET SAADAAN NÄKYVILLE
                // jos käyttäjä on tunnistanut oikeat liput
                if (correctAnswers == SIGNS_IN_QUIZ) {
                    staticTotalGuesses = totalGuesses; // tämä muuttuja pitää lisätä luokan määrittelyihin (ei ollut aiemmassa versiossa).
                    // DialogFragment näyttää tilastot ja käynnistää uuden visan
                    DialogFragment quizResults = new MyAlertDialogFragment();
                    // System.out.println(" Testi tuloksia tassa on " + totalGuesses);
                    // käytetään FragmentManager:ia näyttämään DialogFragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                    resetQuiz();

                } else { // vastaus on oikein, mutta visailu jatkuu
                    // ladataan seuraava lippu 2 s viiveen jälkeen
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    animate(true); // animoidaan lippu pois
                                }
                            }, 2000); // 200 ms viive
                }
            } else { // vastaus väärin
                signImageView.startAnimation(shakeAnimation); // ravistetaan

                // näytetään teksti "Incorrect" punaisella
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(
                        R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false); // estetään väärä vastaus
            }
        }

    };

    // Tehdään aito sisäluokka, kun anonyymi sisäluokka ei toiminut
    // ohjeistuksen mukaisesti sen pitää olla staattinen
    public static class MyAlertDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setMessage(
                    getString(R.string.results,
                            MainActivityFragment.staticTotalGuesses,
                            (1000 / (double) staticTotalGuesses)));

            // Reset Quiz painike
            builder.setPositiveButton(R.string.reset_quiz,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //System.out.println(" Testi tuloksia keskella " + MainActivityFragment.staticTotalGuesses);
                        }
                    }
            );
            // System.out.println(" tuloksia alla " + MainActivityFragment.staticTotalGuesses);
            return builder.create();
        }

    }

    // metodi joka asettaa Buttonit toimimattomiksi
    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
        }
    }

}
