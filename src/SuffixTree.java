import java.util.ArrayList;

public class SuffixTree {

    private ArrayList<Edge> treeEdges;

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
     * Instantiates an empty tree
     * @param
     * @return an instance of a SuffixTree object with a null tree
     */
    public SuffixTree(){

        this.treeEdges = new ArrayList<>();

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

        this.treeEdges.add(new Edge(str.substring(stringLength-1,stringLength), 0, endMax));

        for(int i = stringLength-2; i >= 0; i--) {

            substr = str.substring(i,stringLength);
            ArrayList<Edge> tempEdges = new ArrayList<>();
            ArrayList<Integer> edgesToRemove = new ArrayList<>();

            for(int k = 0; k < this.treeEdges.size(); k++) {

                Edge edge = this.treeEdges.get(k);

                int maxSimilarityIndex = longestEquivalentStartString(substr, edge.value);

                if(maxSimilarityIndex > 0) {

                    // We have found a match so we split
                    String longestMatch = substr.substring(0, maxSimilarityIndex);
                    String branchOne = substr.substring(maxSimilarityIndex, substr.length());
                    String branchTwo = edge.value.substring(maxSimilarityIndex, edge.value.length());

                    // Remove old edge and split with the new criteria
                    tempEdges.add(new Edge(longestMatch, edge.begin, edge.end));
                    tempEdges.add(new Edge(branchOne, edge.end, ++endMax));
                    tempEdges.add(new Edge(branchTwo, edge.end, ++endMax));
                    edgesToRemove.add(k);

                }

            }

            if(tempEdges.size() == 0) {
                this.treeEdges.add(new Edge(substr, 0, ++endMax));
            }
            else {
                for (int d = 0; d < edgesToRemove.size(); d++) {

                    this.treeEdges.remove(edgesToRemove.get(d));

                }

                this.treeEdges.addAll(tempEdges);
                tempEdges.clear();
            }

        }

    }

    public void traverseSuffixTree() {

        for(int i = 0; i < this.treeEdges.size(); i++) {

            System.out.print(this.treeEdges.get(i).begin);
            System.out.print(" ");
            System.out.print(this.treeEdges.get(i).end);
            System.out.print(" ");
            System.out.print(this.treeEdges.get(i).value);
            System.out.print("\n");

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
