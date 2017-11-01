public class Huffman {
	private int[] frequency;
	
	Huffman(){
		//an array where each index represents the corresponding ascii value that stores the frequency of ascii value presence.
		frequency = new int[128];
	}

	public int[] findFrequencies(String file) {
		for (int i = 0 ; i < file.length(); i++) {
			char c = file.charAt(i);
			int ascii_num = Character.getNumericValue(c);
			//increment the frequency of said ascii value. 
			this.frequency[ascii_num]++;
		}
		return this.frequency;
	}
	
	public void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}
	
	public static void main(String args[]) {
		Huffman h = new Huffman();
		String test_string = "hellomynameispeter";
		h.printArray(h.findFrequencies(test_string));
	}
}
