# Assignment 4. Patient Zero

Student ID: 252119  
Name: Nataliyev Daniyar  
Group: SE-2536

Repository: https://github.com/Natalyev/assignment4DAA

Java 17 or newer. The project compares two virus strains, searches for a marker and chooses a minimum-cost set of people to test.

## Run

Run from the patient-zero folder:

```powershell
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Open `output/outbreak.html` in a browser. Personal input is included; to regenerate it, run `java tools/MakeVariant.java 252119 data`.

## Program output

```text
Mutations: 83
Shared core: 1062
Marker hits: 4
First hit: 2265
Border length: 4
Shared words: 465
Tests needed: 42
Cheapest testing: 165
Longest chain: 32
```

The strains have 1117 and 1121 letters, the genome has 26656 letters, and the tree has 90 people. Positions start at zero, shared words count starting positions in A, and the longest chain counts links rather than people.

## Lab screenshot

The screenshot below shows the lab console summary on my personal data. All five missions and all nine output values pass the lab checks. The interactive page is `output/outbreak.html`.

![Patient Zero lab console: all nine results pass](output/outbreak.png)

## Questions

### Mission 1

`d[i][j]` is the minimum number of changes needed to turn the first i letters of A into the first j letters of B. I compare deletion from the cell above plus 1, insertion from the cell on the left plus 1, and replacement from the diagonal plus 1 (or plus 0 for equal letters). The first column is i and the first row is j because converting to or from an empty string requires deleting or inserting every letter.

### Mission 2

A subsequence can skip letters, but it must keep their order; a substring must be consecutive. An insertion or deletion can shift matching letters to different positions, so checking only equal positions loses matches. For this input the LCS is 1062, while only 394 letters match at the same index.

### Mission 3

`pi[i]` is the length of the longest proper prefix of the marker up to i that is also its suffix. On a mismatch KMP uses `pi[j - 1]` to keep the part already known to match, so the text pointer never goes backwards; after a full match the same fallback allows overlaps. My marker is `GAGATCGAGA`, and its border length is 4 because `GAGA` is both its beginning and its ending.

### Mission 4

Different words can have the same hash, so I compare their letters before counting a match. The hash map stores all B positions for each hash, and every matching A position counts once even if its word occurs several times in B. Rolling each hash takes constant time, but many false hash matches can require checking every pair of windows, taking up to O(a*b*k) time.

### Mission 5

`take[v]` is the minimum cost for the subtree when v is tested, and `skip[v]` is the minimum when v is not tested: take is `cost[v] + sum(min(take[child], skip[child]))`, while skip is `sum(take[child])`. I choose the smaller root state and rebuild the set: an untested parent forces every child to be tested, while a tested parent allows the cheaper child state. Choosing the person with most links can fail: in a star with three leaves, if the centre costs 100 and each leaf costs 1, testing the leaves costs 3 instead of 100.

For the longest chain, each vertex returns its longest downward branch. The sum of the two longest branches gives the longest path through that vertex; the maximum over all vertices is the answer in links.

## Timing (Mission 7)

Measured with the unmodified Bench on Windows and JDK 25. Values are milliseconds, averaged over 3 runs by the provided program. The source also compiles with `--release 17`.

| test | size | time (ms) |
|---|---:|---:|
| naive search | 20000 | 61.6 |
| naive search | 40000 | 107.6 |
| naive search | 80000 | 198.6 |
| KMP | 20000 | 3.2 |
| KMP | 40000 | 1.8 |
| KMP | 80000 | 0.5 |
| edit distance | 500 | 7.3 |
| edit distance | 1000 | 6.6 |
| edit distance | 2000 | 15.6 |
| edit distance | 4000 | 75.0 |

The naive search nearly doubles here (about 1.75x and 1.85x) because the pattern length stays fixed at 2000, though its general worst-case cost is O(n*m), compared with O(n+m) for KMP. The small KMP measurements decrease because JVM warm-up and measurement noise dominate these short runs, so they do not demonstrate decreasing algorithmic complexity. Edit distance fills a quadratic table: doubling both string lengths should roughly quadruple the work, although the measured ratios (about 0.90x, 2.36x and 4.81x) also reflect warm-up, allocation and garbage collection.

## Complexity

Here a and b are the strain lengths, n is text length, m is marker length, k is window length, h is the number of hits, and v is the number of people. Space includes the returned results and recursion stack where applicable.

| algorithm | time | space | assumptions |
|---|---|---|---|
| Edit distance | O(a*b) | O(a*b) | Full table; precisely O((a+1)(b+1)) including empty inputs |
| LCS | O(a*b) | O(a*b) | Same table dimensions; constant work per cell |
| Prefix function | O(m) | O(m) | Fallbacks use earlier prefix values |
| KMP including prefix function | O(n+m) | O(m+h) | All hits stored; overlaps included |
| Rabin-Karp shared words | Expected O(a+b+a*k); worst O(a+b+a*b*k) | O(a+b) | Expected constant hash-map lookup and few false collisions; each candidate can need k comparisons; k=12 here |
| Tree testing DP and reconstruction | O(v) | O(v) | Valid tree; each link processed a constant number of times |
| Longest chain | O(v) | O(v) | Two deepest branches per vertex; recursion can reach v |

The tree methods use recursion as suggested by the starter for about 100 people. A very deep tree far beyond the assignment sizes would need an iterative traversal to avoid stack overflow.
