# Word Count Challenge

See: https://benhoyt.com/writings/count-words/

Java implementation: https://github.com/bit-twit/countwords

### TODO

1. Finish current implementation
2. write formal interface specs
3. write functional tests against interface using Spock
4. write new design in Groovy 4
    - 16 byte node chunks
    - node is 1..4 chunks
    - free lists based on # contiguous chunks
    - buffer is long[]; each chunk is long[2]
    - chunk[0][0]:
        - 1 b free flag: 0===free, 1===inuse
        - 5 b : number of 1 bits in alpha mask
        - 26 b : alpha mask; each 1 bit corresponds to an inuse letter
        - 2 x 2 b : two next-node indices
    - chunk[0][1] : 4 x 2 b : next node indices
    - chunk[1] : 8 x 16 b : 8 next-node
    - chunk[0] flags + 6 next nodes : 0..5
    - chunk[1] : 8 next nodes : 6..13
    - chunk[2] : 8 next nodes : 14..21
    - chunk[3][0] : 4 next nodes : 22..25
    - chunk[3][1] : unused !!!! -- find a use :)
    - separate last letter/count array: int[]
        - prefix node + last letter + word count
        - lookup by prefix node
5. make new design thread safe : AtomicLongs for free pointers
6. MemoryMapped buffer for input file
7. Bonus: SplitIterator impl that will allow parallel streams !!!
8. use parallel streams to process