## Team Number 8: Deduplicator
## Conrad Liu, Esen Harris, Josh Surette, Khai Phan, Peter Jang

### Problem:

This program seeks to compress and store a multitude of files in one location (henceforth called a locker), as well as retrieving compressed files to a specified location. Additionally, the program can retrieve storage statistics, ie. how many bytes are currently stored in a locker. These lockers are portable, and can be copied to another machine and accessed there. The aforementioned features are achievable via a CLI.

The program is also capable of deleting specific files, can compress and retrieve files in a locker via a GUI, and can also compress directories of files as one entity.

### Implementation:

#### Command Line:

The command line serves as the entry point for the program. A user can enter specific flags to either store and retrieve compressed files, and initialize/create lockers. These flags are:

`-addFile`: This must be used alongside the `-locker` flag, and will add a compressed file to the locker of interest.

`-locker`: If the locker location does not yet exist, a new locker will be created an initialized. Can be used standalone or with `-addFile`

`-retrieve`: Must be accompanied with the `-dest` flag. The argument to this flag is expected to be a compressed file in the .dedup format.

`-dest`: Must be accompanied with the `-retrieve` flag. The accompanying uncompressed file will be written to the argument of this flag.

`-delete`: Specifies the compressed file within a locker to be deleted. File format MUST be in the form .dedup.

#### Suffix Tree Compression:


#### Compression Metadata:

There are two sources of metadata that allow for the proper compression/decompression of files within a locker:

##### .anchor:
-   Located in the root directory of a locker, this file contains the contents of the reference file that will be used to find LCS's to compress a given file. The contents of the first file added to a locker will always be used as the anchor file.

##### .dedup:
-   Filenames with this extension consist of the resulting string following compression of that same filename. Used in conjunction with filenames containing the .meta extension.

##### .meta:
-   Filenames with this extension contain meta information for the corresponding compressed file. Each line contains information in the format,
```filenameIndex[anchorStartIndex, anchorEndIndex],```
anchorStartIndex/anchorEndIndex refer to the begin and end indexes of a substring of the anchor file. filenameIndex is the index of the compressed file to add this substring.

#### Example:
##### .anchor:
"helloworld"

##### helloworld.txt:
"hello1world2"

##### helloworld.txt.meta:
-   `0:[0, 5], // place "hello" in index 0 of helloworld.txt.dedup`
-   `6:[6, 10],	// place "world" in index 6 of helloworld.txt.dedup`

##### helloworld.txt.dedup:
12

Notes:
-   We chose to use the contents of the first file added to a locker as our .anchor content because it allowed for a trivial implementation of compression and file deletion.
    -   This implementation means that the compressed file for a filename whose contents are in .anchor will simply be an empty string.
    -   Storing the contents of this first file inside an .anchor file, it is also trivial to delete the original file `foo` whose contents are in .anchor, since all other files depend on the .anchor file, and not the original file `foo` where the content lives.
-   The .meta text uses indexes of the substrings in .anchor over the actual substrings since it massively saves on file size. There is no need to duplicate any information that is already located inside .anchor.


#### Decompression:

The decompression of a file is made possible by utilizing the .anchor file, and inserting its substrings into the indexes indicated by the .meta file.
-   Specifically, for the compressed contents contained in compressed file `foo.dedup`:
    -   For each line in `foo.meta`:
        -   Extract line info in the form `filenameIndex[anchorStartIndex, anchorEndIndex],`
        -   Insert substring in .anchor from `anchorStartIndex` to `anchorEndIndex` to index `filenameIndex` of the compressed file

### Work Breakdown:

-   Conrad Liu: Worked alongside Peter Jang in implementing compression/decompression metadata features, and expanded on the CLI to implement additional commands. Wrote README.md file and INSTALL.txt file.
-   Esen Harris: Worked on the development of the compression algorithm and the decompression algorithms. Organized the algorithm that used the LCS to compress the file by deleting the lcs from the file. Then, we would use the reference of the lcs and create meta data for the deduplicator to read off of. Moral Support of the suffix tree. Developed the GUI that implements New Locker, Deduplicator, Delete and Decompress.
-   Josh Surette: Built the SuffixTree data structure, and implemented feature to store directories of files as a single entity.
-   Khai Phan: Implemented SuffixTree data structure traversal. Also helped with general design scheme for most additional features.
-   Peter Jang: Worked alongside Conrad Liu to implement compression/decompression metadata features and CLI command processing, and helped implement memory management techniques to handle large files.


### GUI IMPLEMENTATION
-   The Lockers Will Be Stored where the Project Folder Lies
-   PUSH BUTTONS:
    -   NEW or SET Locker
        - Creates a Locker or Sets your root locker to a locker that already exists
    -   Deduplicate
        - Runs the compression Algorithm using the LCS and stores your selected filesinto the locker that you set as the root.
    -   Delete
        - Delete Files within your Locker.
        - Select the '.dedup' of the file that you would like to delete
    -   Decompress
        - Go to the locker that contains the files that you would like to decompress
        - Select any files that you would like to decompress