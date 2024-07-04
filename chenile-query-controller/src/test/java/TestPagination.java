public class TestPagination {
	public void calculate(int currentPage,int maxRows, int numRowsInPage) {
		
		
		
		int maxPages = Math.round(maxRows/numRowsInPage) + 1;
		currentPage = (currentPage > maxPages )? maxPages: currentPage;
		int startRow = (currentPage-1)*numRowsInPage+1;
		
		System.out.println("Start row is " + startRow);
		System.out.println("Max Pages is " + maxPages);
	}
	
	public static void main(String[] args) {
		TestPagination tp = new TestPagination();
		tp.calculate(1,41,20);
		
		tp.calculate(3,41,20);
		
		tp.calculate(4,41,20);
	}

}
