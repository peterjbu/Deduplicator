import java.util.ArrayList;
import java.util.HashSet;

public class SuffixTree {

    private ArrayList<Node> tree;

    class Node {

        private int value;
        private HashSet<String> edges;

        Node(int value) {
            this.value = value;
            this.edges = new HashSet<>();
        }

        void addEdge(String edge) {
            this.edges.add(edge);
        }

    }

    /**
     * Converts an array of strings into one concatenated string delimited with special characters to create a suffix tree
     * @param
     * @return an instance of a SuffixTree object with a null tree
     */
    public SuffixTree(){

        this.tree = new ArrayList<>();

    }

    /**
     * Converts an array of strings into one concatenated string delimited with special characters to create a suffix tree
     * @param stringArrayList is the array of strings to be concatenated
     * @return concatString returns the array of strings concatenated and separated with special '#i' symbols
    */
    public String concatStringWithSpecialCharacter(ArrayList<String> stringArrayList) {

        int arrListSize = stringArrayList.size();

        if(arrListSize == 1) {
            return stringArrayList.get(0);
        }
        else if(arrListSize < 1) {
            return "";
        }

        String concatString = "";

        for(int i = 0; i < arrListSize; i++) {

            String codedSeparator = "#".concat(String.valueOf(i));
            concatString = concatString.concat(stringArrayList.get(i).concat(codedSeparator));

        }

        return concatString;

    }

    /**
     * Converts an array of strings into one concatenated string delimited with special characters to create a suffix tree
     * @param str is a single, continuous string
     * @return SuffixTree resulting from the str
     */
    public Node buildTree(String str) {

        int stringLength = str.length();
        String substr;

        SuffixTree st = new SuffixTree();
        tree.add(new Node(0));

        // Loop through each character and see if it exists in the current tree, if so append character
        for(int i = stringLength-1; i >= 0; i--) {

            substr = str.substring(i,stringLength);
            tree.get(0).addEdge(substr);

        }

        return tree.get(0);

    }

    public void traverseSuffixTree(SuffixTree st) {

        for(int i = 0; i < st.tree.size(); i++) {

            System.out.print(st.tree.get(i).edges);

        }

    }

    // Unit tests for SuffixTree class in driver
    public static void main(String args[]) {

        ArrayList<String> testList = new ArrayList<String>();
        testList.add("Josh");

        SuffixTree st = new SuffixTree();
        st.buildTree(st.concatStringWithSpecialCharacter(testList));

        st.traverseSuffixTree(st);

    }

}
