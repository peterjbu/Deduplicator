import com.sun.javafx.geom.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SuffixTree {

    private ArrayList<ArrayList<Edge>> arr;

    SuffixTree() {
        this.arr = new ArrayList<>();
        this.arr.add(new ArrayList<>());
        this.arr.add(new ArrayList<>());
    }

    public void addEdge(Edge e) {

        if(this.arr.size() < e.end) {
            this.arr.add(e.end, new ArrayList<>());
        }

        this.arr.get(e.begin).add(e); // Adds outgoing edge

    }

    class Edge {

        private String value;
        private int begin;
        private int end;

        Edge(String value, int begin, int end) {
            this.value = value;
            this.begin = begin;
            this.end = end;
        }

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
     * Creates a SuffixTree from a given string or a string made from a concatenated set of strings
     * @param str is a single, continuous string
     * @return SuffixTree resulting from the str
     */
    public void buildTreeEdges(String str) {

        int stringLength = str.length();
        String substr;
        int endMax = 1;

        // First start by adding the last letter in the string to the Tree
        this.addEdge(new Edge(str.substring(stringLength-1,stringLength), 0, endMax));

        // Outer loop focuses on iterating through the to get all substrings
        for(int i = stringLength-2; i >= 0; i--) {

            substr = str.substring(i,stringLength);

            // The inner loop focuses on iterating through the existing edges to find either splits, or new edges to add
            splitEdges(this.arr, substr, this.arr.get(0), endMax);

        }

    }

    public void splitEdges(ArrayList<ArrayList<Edge>> st, String substring, ArrayList<Edge> outgoingEdges, int endMax) {

        for(int i = 0; i < outgoingEdges.size(); i++) {

            String edgeString = outgoingEdges.get(i).value;
            int maximumSimilarity = longestEquivalentStartString(substring, edgeString);

            if(maximumSimilarity > 0) {

                if (maximumSimilarity == edgeString.length()) { // Could be further down the branch

                    splitEdges(st, substring, st.get(outgoingEdges.get(i).end), endMax);

                }
                else {

                    String longestMatch = substring.substring(0, maximumSimilarity);

                    st.get(outgoingEdges.get(i).end).add(new Edge(substring.substring(maximumSimilarity,substring.length()), outgoingEdges.get(i).end, ++endMax));
                    st.get(outgoingEdges.get(i).end).add(new Edge(outgoingEdges.get(i).value.substring(maximumSimilarity,outgoingEdges.get(i).value.length()), outgoingEdges.get(i).end, ++endMax));
                    outgoingEdges.get(i).value = longestMatch;
                    return;

                }

            }

        }

        // No Similarities were found, add edge to the root node
        Edge e = new Edge(substring, 0, ++endMax);

    }

    public void traverseSuffixTree() {

        for(int i = 0; i < this.arr.size(); i++){
            System.out.println(this.arr.get(i));
        }

    }

    public static int longestEquivalentStartString(String one, String two) {

        int oneLen = one.length();
        int twoLen = two.length();

        int maximum = 0;
        int minLength;

        if(oneLen < twoLen) {
            minLength = oneLen;
        }
        else if(oneLen > twoLen) {
            minLength = twoLen;
        }
        else {
            minLength = oneLen;
        }

        for(int i = 0; i < minLength; i++) {

            if(one.charAt(i) == two.charAt(i)) {

                maximum++;

            }
            else {

                break;

            }

        }

        return maximum;

    }

    // Unit tests for SuffixTree class in driver
    public static void main(String args[]) {

        ArrayList<String> testList = new ArrayList<String>();
        testList.add("josh0jos1jo2");

        SuffixTree st = new SuffixTree();
        String str = st.concatStringWithSpecialCharacter(testList);

        st.buildTreeEdges(str);
        st.traverseSuffixTree();

    }

}

