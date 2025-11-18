  package sudocugame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class SudocuGame extends JFrame{
  private int n, sq;
  private JTextField[][] cells;
  private int[][] board, solution, initialBoard;
  private boolean isChecked=false, isDarkMode=false;
  private Color lightBg=Color.WHITE, lightFg=Color.BLACK, darkFg=Color.WHITE, darkBg=new Color(45, 45, 45);
  private int selectedRow = -1, selectedCol = -1;
  private int hint=0;
  private JButton hintBtn;
  private Timer timer;
  private int timeleft;
  private JLabel timerLabel;
  private int timenow=0;

  // Constructor
  public SudocuGame(){
    // Get user input for n
    n=getSudokuSize();
    sq=(int)Math.sqrt(n);

    // Initialize arrays based on n
    cells=new JTextField[n][n];
    board=new int[n][n];
    solution=new int[n][n];
    initialBoard=new int[n][n];

    setTitle("Sudoku Game - by Saiful Islam");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(940, 940);
    setLayout(new BorderLayout());

    JPanel gridPanel=new JPanel(new GridLayout(n, n));
    Font font=new Font("SansSerif", Font.BOLD,  20);

    for(int row=0; row<n; row++){
      for(int col=0; col<n; col++){
        JTextField field=new JTextField();
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setFont(font);
        int top=(row%sq==0)?4:1;
        int left=(col%sq==0)?4:1;
        int bottom=(row==n-1)?4:1;
        int right=(col==n-1)?4:1;
        field.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
        field.setBackground(getBackgroundColor());
        field.setForeground(getForegroundColor());
        
        int r = row, c = col;
        field.addMouseListener(new java.awt.event.MouseAdapter() {
          @Override
          public void mouseClicked(java.awt.event.MouseEvent e) {
            selectedRow = r;
            selectedCol = c;
          }
        });

        cells[row][col]=field;
        gridPanel.add(field);
      }
    }

    // Difficulty buttons
    JButton easyBtn=new JButton("Easy");
    JButton mediumBtn=new JButton("Medium");
    JButton hardBtn=new JButton("Hard");
    JButton darkModeBtn=new JButton("Toggle Dark Mode");
    
    easyBtn.addActionListener(e ->{
      timenow=0;
      startTimer(900);
      timerLabel.setText("Time: 15:00");
      loadPuzzleWithDifficulty(n*n*3/4);
      resetColors();
      hint=5;
      hintBtn.setEnabled(true);
      hintBtn.setText("Hint: " + hint);
    });
    mediumBtn.addActionListener(e -> {
      timenow=1;
      startTimer(600);
      timerLabel.setText("Time: 10:00");
      loadPuzzleWithDifficulty(n*n/2);
      resetColors();
      hint=4;
      hintBtn.setEnabled(true);
      hintBtn.setText("Hint: " + hint);
    });
    hardBtn.addActionListener(e -> {
      timenow=-1;
      startTimer(300);
      timerLabel.setText("Time: 05:00");
      loadPuzzleWithDifficulty(n*n/3+1);
      resetColors();
      hint=3;
      hintBtn.setEnabled(true);
      hintBtn.setText("Hint: " + hint);
    });

    JButton solveBtn=new JButton("Solve");
    JButton clearBtn=new JButton("Clear");
    JButton checkBtn=new JButton("Check");
    JButton homeBtn=new JButton("Home");
    hintBtn=new JButton("Hint");
    
    solveBtn.addActionListener(e ->{
        stopTimer();
      int[][] tempBoard=new int[n][n];
      copyBoard(initialBoard, tempBoard);
      if (solveSudoku(tempBoard)){
        updateGrid(tempBoard);
        isChecked=false;
      } else{
        JOptionPane.showMessageDialog(this, "No solution found!");
      }
    });

    clearBtn.addActionListener(e ->{
      clearGrid();
      isChecked=false;
      stopTimer();
      if(timenow==-1){
        startTimer(300);
        timerLabel.setText("Time: 05:00");
      }else if(timenow==1){
        startTimer(600);
        timerLabel.setText("Time: 10:00");
      }else{
        startTimer(900);
        timerLabel.setText("Time: 15:00");
      }

    });

    checkBtn.addActionListener(e ->{
      if(!isChecked){
        int[][] tempBoard=new int[n][n];
        copyBoard(initialBoard, tempBoard);
        if(solveSudoku(tempBoard)){
          copyBoard(tempBoard, solution);
        }
        readFromGrid();
        for (int i=0; i<n; i++){
          for (int j=0; j<n; j++){
            String text=cells[i][j].getText().trim();
            if (text.isEmpty()){
              cells[i][j].setBackground(getBackgroundColor());
              continue;
            }
            try{
              int userVal=Integer.parseInt(text);
              if (userVal==solution[i][j]){
                switch (userVal) {
                    case 1:
                      cells[i][j].setBackground(new Color(52, 123, 218));   // Dark Blue (#347BDA)
                      break;
                    case 2:
                      cells[i][j].setBackground(new Color(37, 168, 88));    // Dark Green (#25A858)
                      break;
                    case 3:
                      cells[i][j].setBackground(new Color(233, 167, 47));   // Dark Gold (#E9A72F)
                      break;
                    case 4:
                      cells[i][j].setBackground(new Color(203, 52, 159));   // Vibrant Purple (#CB349F)
                      break;
                    case 5:
                      cells[i][j].setBackground(new Color(255, 102, 67));   // Strong Orange (#FF6643)
                      break;
                    case 6:
                      cells[i][j].setBackground(new Color(46, 147, 185));   // Deep Cyan (#2E93B9)
                      break;
                    case 7:
                      cells[i][j].setBackground(new Color(120, 70, 240));   // Bold Indigo (#7846F0)
                      break;
                    case 8:
                      cells[i][j].setBackground(new Color(160, 212, 59));   // Medium Olive (#A0D43B)
                      break;
                    case 9:
                      cells[i][j].setBackground(new Color(255, 49, 72));    // Vivid Red (#FF3148)
                      break;
                    default:
                      // ভুল বা খালি হলে error red (লাল) ব্যবহার করুন
                       cells[i][j].setBackground(new Color(200, 0, 20)); // Error Red
                      break;
                  }

                //cells[i][j].setBackground(Color.GREEN);
              }else
                cells[i][j].setBackground(Color.RED);
            }catch(NumberFormatException ex) {
              cells[i][j].setBackground(new Color(255, 182, 193));
            }
          }
        }
        
        // after coloring/setting backgrounds, check for full correct solution
        boolean allCorrect = true;
        readFromGrid(); // fills board[][]
        for (int i = 0; i < n && allCorrect; i++) {
          for (int j = 0; j < n; j++) {
            if (board[i][j] == 0 || board[i][j] != solution[i][j]) {
              allCorrect = false;
              break;
            }
          }
        }
        if (allCorrect) {
          // user solved within the time
          stopTimer();
          JOptionPane.showMessageDialog(this, "Congratulations!! You Won!!");
          // optionally, lock the grid so they can't edit after win:
          for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
              cells[i][j].setEditable(false);
        }

      } else{
        resetColors();
      }
      isChecked=!isChecked;
    });

    darkModeBtn.addActionListener(e ->{
      toggleDarkMode();
      resetColors();
    });

    homeBtn.addActionListener(e ->{
        stopTimer();
      this.dispose();
      SwingUtilities.invokeLater(SudocuGame::new);
    });
    
    hintBtn.addActionListener(e -> hint());

    
    timerLabel = new JLabel("Time: 00:00");
    timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
      

    JPanel controlPanelTop=new JPanel();
    controlPanelTop.add(easyBtn);
    controlPanelTop.add(mediumBtn);
    controlPanelTop.add(hardBtn);
    controlPanelTop.add(darkModeBtn);

    JPanel controlPanelBottom=new JPanel();
    controlPanelBottom.add(solveBtn);
    controlPanelBottom.add(clearBtn);
    controlPanelBottom.add(checkBtn);
    controlPanelBottom.add(homeBtn);

    controlPanelTop.add(timerLabel);
      
//    JPanel controlPanelBottom2=new JPanel();
    controlPanelBottom.add(hintBtn);
    
    add(gridPanel, BorderLayout.CENTER);
    add(controlPanelTop, BorderLayout.NORTH);
    add(controlPanelBottom, BorderLayout.SOUTH);
//    add(controlPanelBottom2, BorderLayout.EAST);

    // resetColors();
    setVisible(true);
  }
  
  // --- Timer Logic ---
    private void startTimer(int seconds) {
      timeleft = seconds;
      if (timer != null) timer.stop();

      timer = new Timer(1000, e -> {
        if (timeleft <= 0) {
          timer.stop();
          timerLabel.setText("Time: 00:00");
          JOptionPane.showMessageDialog(this, "⏳ Time's up!\n    You Lose!!");
          return;
        }
        timeleft--;
        
        int minutes=timeleft/60;
        int secondsLeft=timeleft%60;

        timerLabel.setText(String.format("Time: %02d:%02d", minutes, secondsLeft));
      });

      timer.start();
    }
    private void stopTimer(){
        if(timer!=null){
            timer.stop();
            timer=null;
        }
    }
    
  // --- User Input for n ---
  private int getSudokuSize(){
    int n=0;
    while(true){
      String input=JOptionPane.showInputDialog(null, 
        "Enter Sudoku size n(4, 9 or 16):",
        "Sudoku Size Input", JOptionPane.QUESTION_MESSAGE);
      
      if(input==null){ // User pressed cancel
        System.exit(0);
      }
        
      try{
        n=Integer.parseInt(input);
        // int sqrt=(int)Math.sqrt(n);
        if(n==4 || n==9 || n==16) break; // valid n
        else{
          JOptionPane.showMessageDialog(null, 
            "Invalid input! n must be 4, 9 or 16", 
            "Error", JOptionPane.ERROR_MESSAGE);
        }
      }catch(NumberFormatException e){
        JOptionPane.showMessageDialog(null, 
          "Please enter a valid integer.", 
          "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    return n;
  }


  private void toggleDarkMode(){
    isDarkMode=!isDarkMode;
    for (int i=0; i<n; i++)
      for (int j=0; j<n; j++){
        cells[i][j].setBackground(getBackgroundColor());
        cells[i][j].setForeground(getForegroundColor());
      }
    getContentPane().setBackground(isDarkMode ? darkBg : lightBg);
    repaint();
  }

  private Color getBackgroundColor(){
    return isDarkMode ? darkBg : lightBg;
  }

  private Color getForegroundColor(){
      return isDarkMode ? darkFg : lightFg;
  }
  
  private void hint() {
    if (selectedRow == -1 || selectedCol == -1) {
      // JOptionPane.showMessageDialog(this, "Please click a cell to get a hint!");
      return;
    }

    if (!cells[selectedRow][selectedCol].getText().trim().isEmpty()) {
      // JOptionPane.showMessageDialog(this, "This cell is already filled!");
      return;
    }
    hint--;
    hintBtn.setText("Hint: " + hint);
    if(hint<=0){
        hintBtn.setEnabled(false);
        return;
    }

    int[][] tempBoard = new int[n][n];
    copyBoard(initialBoard, tempBoard);
    solveSudoku(tempBoard);

    cells[selectedRow][selectedCol].setText(String.valueOf(tempBoard[selectedRow][selectedCol]));
    cells[selectedRow][selectedCol].setForeground(Color.BLUE);
    board[selectedRow][selectedCol] = tempBoard[selectedRow][selectedCol];
  }



  private void clearGrid(){
    for (int i=0; i<n; i++)
      for (int j=0; j<n; j++){
        // cells[i][j].setText("");
        // cells[i][j].setBackground(getBackgroundColor());
        // board[i][j]=0;
        if (initialBoard[i][j] == 0) {
          cells[i][j].setText("");
          cells[i][j].setEditable(true);
        } else {
          cells[i][j].setText(String.valueOf(initialBoard[i][j]));
          cells[i][j].setEditable(false);
        }
        cells[i][j].setBackground(getBackgroundColor());
        board[i][j] = initialBoard[i][j];
      }
  }

  private void updateGrid(int[][] targetBoard){
    for(int i=0; i<n; i++)
      for(int j=0; j<n; j++){
        if(targetBoard[i][j] !=0)
          cells[i][j].setText(String.valueOf(targetBoard[i][j]));
        else
          cells[i][j].setText("");
      }
  }

  private void readFromGrid(){
    for (int i=0; i<n; i++)
      for (int j=0; j<n; j++){
        String text=cells[i][j].getText().trim();
        if (text.isEmpty()){
          board[i][j]=0;
        }else{
          try {
            int val=Integer.parseInt(text);
            if(val>=1 && val<=n) board[i][j]=val;
            else board[i][j]=0;
          } catch (NumberFormatException e){
            board[i][j]=0;
          }
        }
      }
  }

  private void resetColors(){
    for (int i=0; i<n; i++)
      for (int j=0; j<n; j++){
      cells[i][j].setBackground(getBackgroundColor());
      cells[i][j].setForeground(getForegroundColor());
      }
  }

  private void copyBoard(int[][] src, int[][] dest){
    for(int i=0; i<n; i++)
      System.arraycopy(src[i], 0, dest[i], 0, n);
  }

  private boolean isValid(int row, int col, int num, int[][] grid){
    for(int x=0; x<n; x++)
      if(grid[row][x]==num || grid[x][col]==num)
        return false;

    int startRow=row/sq*sq, startCol=col/sq*sq;
    for(int i=startRow; i<startRow+sq; i++)
      for(int j=startCol; j<startCol+sq; j++)
        if(grid[i][j]==num) return false;
    return true;
  }

  private boolean solveSudoku(int[][] grid){
    for (int row=0; row<n; row++)
      for (int col=0; col<n; col++)
        if (grid[row][col]==0){
          for (int num=1; num<=n; num++){
            if (isValid(row, col, num, grid)){
              grid[row][col]=num;
              if (solveSudoku(grid)) return true;
              grid[row][col]=0;
            }
          }
          return false;
        }
    return true;
  }

   private boolean fillBoard(int[][] grid){
    List<Integer> nums=new ArrayList<>();
    for(int i=1; i<=n; i++) nums.add(i);

    for(int row=0; row<n; row++)
      for(int col=0; col<n; col++)
        if(grid[row][col]==0){
          Collections.shuffle(nums);
          for(int num : nums){
            if(isValid(row, col, num, grid)){
              grid[row][col]=num;
              if(fillBoard(grid)) return true;
              grid[row][col]=0;
            }
          }
          return false;
        }
    return true;
  }

  private int[][] generateSudokuPuzzle(int clues){
    int[][] fullBoard=new int[n][n];
    fillBoard(fullBoard);
    int[][] puzzle=new int[n][n];
    copyBoard(fullBoard, puzzle);
    int cellsToRemove=n*n-clues;
    Random rand=new Random();

    while(cellsToRemove > 0){
      int row=rand.nextInt(n);
      int col=rand.nextInt(n);
      if(puzzle[row][col] !=0){
        puzzle[row][col]=0;
        cellsToRemove--;
      }
    }
    // After generating puzzle
    for(int i=0; i<n; i++){
      for(int j=0; j<n; j++){
        initialBoard[i][j]=puzzle[i][j];  // store original
      }
    }
    return puzzle;
  }

  private void loadPuzzleWithDifficulty(int clues){
    int[][] generated=generateSudokuPuzzle(clues);
    copyBoard(generated, board);
    // updateGrid(board);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (board[i][j] != 0) {
          cells[i][j].setText(String.valueOf(board[i][j]));
          cells[i][j].setEditable(false);
        } else {
          cells[i][j].setText("");
          cells[i][j].setEditable(true);
        }
        cells[i][j].setBackground(getBackgroundColor());
        cells[i][j].setForeground(getForegroundColor());
      }
    }
    isChecked=false;
  }

  public static void main(String[] args){
    SwingUtilities.invokeLater(SudocuGame::new);
  }
}
