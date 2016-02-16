package college.invisible.robothello;

/**
 * Created by ppham on 2/8/16.
 */
public class SampleModel {
    private String mQuestion;
    private String mAnswer;

    public enum Side {QUESTION, ANSWER};

    private Side mSide;

    public SampleModel(String s, String s1) {
        this.mQuestion = "new question";
        this.mAnswer = "new answer";
        this.mSide  = Side.QUESTION;

    }


    public void setQuestion(String question) {
        this.mQuestion = question;
    }

    public void setAnswer(String answer) {
        this.mAnswer = answer;
    }

    public String getQuestion() {
        return this.mQuestion;
    }

    public String getAnswer() {
        return this.mAnswer;
    }

    public boolean isQuestion() {
        return this.mSide == Side.QUESTION;
    }

    public String getVisibleString() {
        if (this.mSide == Side.QUESTION) {
            return this.mQuestion;
        } else {
            return this.mAnswer;
        }
    }

    public void toggleSide() {
        if (this.mSide == Side.QUESTION) {
            this.mSide = Side.ANSWER;
        } else {
            this.mSide = Side.QUESTION;
        }
    }
}
