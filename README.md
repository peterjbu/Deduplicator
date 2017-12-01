## Team Number 8: Deduplicator
## Conrad Liu, Esen Harris, Josh Surette, Khai Phan, Peter Jang

### Problem:

This program seeks to compress and store a multitude of files in one location (henceforth called a locker), as well as retrieving compressed files to a specified location. Additionally, the program can retrieve storage statistics, ie. how many bytes are currently stored in a locker. These lockers are portable, and can be copied to another machine and accessed there. The aforementioned features are achievable via a CLI.

In addition, the program is capable of deleting specific files, can compress and retrieve files in a locker via a GUI, and can also compress directories of files as one entity.

### Implementation:

#### Command Line:

The command line serves as the entry point for the program. A user can enter specific flags to either store and retrieve compressed files, and initialize/create lockers. These flags are:

-addFile: This, along with the -locker flag, will add a compressed file to the locker of interest.

-locker: If the locker location does not yet exist, a new locker will be created an initialized.

-retrieve: Must be accompanied with the -dest flag. The argument to this flag is expected to be a compressed file in the .dedup format.

-dest: Must be accompanied with the -retrieve flag. The accompanying uncompressed file will be written to the argument of this flag.

#### Suffix Tree Compression:



#### Compression Metadata:

There are two sources of metadata that allow a locker to function properly:

.anchor:
-   Located in the root directory of a locker, this file contains the contents of the reference file that will be used to find LCS's to compress a given file.

.dedup: Filenames with this extension consist of the resulting string following compression of that same filename. Used in conjunction with filenames containing the .meta extension.

.meta:
-   Filenames with this extension contain meta information for the corresponding compressed file. Each line contains information in the format,
`filenameIndex[anchorStartIndex, anchorEndIndex],`anchorStartIndex/anchorEndIndex refer to the begin and end indexes of a substring of the anchor file. filenameIndex is the index of the compressed file to add this substring.

#### Example:
.anchor: "helloworld"

helloworld.txt: "hello1world2"

helloworld.txt.meta:
-   `0:[0, 5],` //place "hello" in index 0 of hellowworld.txt.dedup
-   `6:[6, 10],`	//place "world" in index 6 of helloworld.txt.dedup

helloworld.txt.dedup: 12

#### Decompression:



1) Suffix tree compression
2) Decompression
3) Metadata


### GUI IMPLEMENTATION
-The Lockers Will Be Stored where the Project Folder Lies
PUSH BUTTONS:
            NEW or SET Locker
                - Creates a Locker or Sets your root locker to a locker that already exists
            Deduplicate
                - Runs the compression Algorithm using the LCS and stores your selected files
                  into the locker that you set as the root.
            Delete
                - Delete Files within your Locker.
                   - Select the '.dedup' of the file that you would like to delete
            Decompress
                - Go to the locker that contains the files that you would like to decompress
                    - select any files that you would like to decompress