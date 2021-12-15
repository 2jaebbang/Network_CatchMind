import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Quiz {
	//String[] quiz=new String[15];
	
	ArrayList<String> quiz = new ArrayList<>();
	ArrayList<ImageIcon> quizImage = new ArrayList<ImageIcon>();
	ArrayList<String> quizHint = new ArrayList<String>();
	public Quiz(){
		quiz.add("지하철");
		quiz.add("수박");
		quiz.add("넷플릭스");
		quiz.add("코카콜라제로");
		quiz.add("자바");
		quiz.add("뉴발란스");
		quiz.add("맥북");
		quiz.add("서울");
		quiz.add("삼다수");
		quiz.add("에펠탑");
		
		quizImage.add(new ImageIcon("src/img/subway.png"));
		quizImage.add(new ImageIcon("src/img/watermelon.jpg"));
		quizImage.add(new ImageIcon("src/img/netflix.png"));
		quizImage.add(new ImageIcon("src/img/cola.png"));
		quizImage.add(new ImageIcon("src/img/java.png"));
		quizImage.add(new ImageIcon("src/img/newbalance.jpg"));
		quizImage.add(new ImageIcon("src/img/macbook.jpg"));
		quizImage.add(new ImageIcon("src/img/seoul.jpg"));
		quizImage.add(new ImageIcon("src/img/samdasu.jpg"));
		quizImage.add(new ImageIcon("src/img/eiffel.jpg"));
		
		quizHint.add("교통");
		quizHint.add("여름");
		quizHint.add("드라마");
		quizHint.add("음료");
		quizHint.add("언어");
		quizHint.add("신발");
		quizHint.add("사과");
		quizHint.add("도시");
		quizHint.add("제주도");
		quizHint.add("랜드마크");
	}

   public ArrayList<String> getQuiz() {
		return quiz;
	}

	public ArrayList<ImageIcon> getQuizImage() {
		return quizImage;
	}

	public ArrayList<String> getQuizHint() {
		return quizHint;
	}

public String setQuiz() {
        int index = (int) (Math.random() * quiz.size());
        System.out.println("quiz is :"+quiz.get(index));
        String tempQuiz = quiz.get(index);
        quiz.remove(index);
        quizImage.remove(index);
        quizHint.remove(index);
        return tempQuiz;
    }
}