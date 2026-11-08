import java.util.*;

public class AlgorithmTest {
    static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    static String dna(Random random, int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) s.append("ACGT".charAt(random.nextInt(4)));
        return s.toString();
    }

    static boolean equal(String a, int i, String b, int j, int k) {
        for (int t = 0; t < k; t++) if (a.charAt(i + t) != b.charAt(j + t)) return false;
        return true;
    }

    static int bruteEdit(String a, String b) {
        if (a.isEmpty()) return b.length();
        if (b.isEmpty()) return a.length();
        int diagonal = bruteEdit(a.substring(1), b.substring(1));
        if (a.charAt(0) == b.charAt(0)) return diagonal;
        return 1 + Math.min(diagonal, Math.min(bruteEdit(a.substring(1), b),
                bruteEdit(a, b.substring(1))));
    }

    static int bruteLcs(String a, String b) {
        int best = 0;
        for (int mask = 0; mask < (1 << a.length()); mask++) {
            int j = 0;
            boolean found = true;
            for (int i = 0; i < a.length(); i++) {
                if ((mask & (1 << i)) == 0) continue;
                while (j < b.length() && b.charAt(j) != a.charAt(i)) j++;
                if (j == b.length()) { found = false; break; }
                j++;
            }
            if (found) best = Math.max(best, Integer.bitCount(mask));
        }
        return best;
    }

    public static void main(String[] args) {
        Random random = new Random(252119);
        for (int test = 0; test < 300; test++) {
            String a = dna(random, random.nextInt(7));
            String b = dna(random, random.nextInt(7));
            check(Strains.editDistance(a, b) == bruteEdit(a, b), "edit distance");
            check(Strains.lcsLength(a, b) == bruteLcs(a, b), "LCS");
            String text = dna(random, random.nextInt(50));
            String p = dna(random, random.nextInt(9));
            ArrayList<Integer> expected = new ArrayList<>();
            for (int i = 0; i + p.length() <= text.length(); i++)
                if (equal(text, i, p, 0, p.length())) expected.add(i);
            int[] hits = Marker.findAll(text, p);
            check(hits.length == expected.size(), "KMP count");
            for (int i = 0; i < hits.length; i++) check(hits[i] == expected.get(i), "KMP position");
            int[] pi = Marker.prefixFunction(p);
            for (int i = 0; i < p.length(); i++) {
                int border = 0;
                for (int k = 1; k <= i; k++)
                    if (equal(p, 0, p, i - k + 1, k)) border = k;
                check(pi[i] == border, "prefix function");
            }
            for (int k = 1; k <= 8; k++) {
                int count = 0;
                for (int i = 0; i + k <= a.length(); i++) {
                    for (int j = 0; j + k <= b.length(); j++) {
                        if (equal(a, i, b, j, k)) { count++; break; }
                    }
                }
                check(Marker.sharedWords(a, b, k) == count, "shared words");
            }
        }
        check(Arrays.equals(Marker.findAll("AAAAA", "AAA"), new int[]{0, 1, 2}), "overlaps");
        check(Marker.sharedWords("AAAAAAAAAAAAAA", "AAAAAAAAAAAA", 12) == 3, "repeated windows");
        // Both words have hash 1704091 with base 257 and modulus 1000000007.
        check(Marker.sharedWords("CGGCATGTATCG", "AGAGAAATTCAA", 12) == 0, "hash collision rejected");
        check(Marker.sharedWords("CGGCATGTATCG", "AGAGAAATTCAACGGCATGTATCG", 12) == 1,
                "real match after a hash collision");
        for (int test = 0; test < 300; test++) {
            int n = 1 + random.nextInt(11);
            int[] parent = new int[n];
            int[] degree = new int[n];
            int[] costs = new int[n];
            for (int v = 1; v < n; v++) {
                parent[v] = random.nextInt(v);
                degree[v]++; degree[parent[v]]++;
            }
            int[][] nb = new int[n][];
            for (int v = 0; v < n; v++) { nb[v] = new int[degree[v]]; costs[v] = random.nextInt(20); }
            Arrays.fill(degree, 0);
            for (int v = 1; v < n; v++) {
                nb[v][degree[v]++] = parent[v];
                nb[parent[v]][degree[parent[v]]++] = v;
            }
            for (int mode = 0; mode < 2; mode++) {
                if (mode == 1) Arrays.fill(costs, 1);
                long best = Long.MAX_VALUE;
                for (int mask = 0; mask < (1 << n); mask++) {
                    boolean covers = true;
                    for (int v = 1; v < n; v++)
                        if ((mask & (1 << v)) == 0 && (mask & (1 << parent[v])) == 0) covers = false;
                    if (!covers) continue;
                    long sum = 0;
                    for (int v = 0; v < n; v++) if ((mask & (1 << v)) != 0) sum += costs[v];
                    best = Math.min(best, sum);
                }
                boolean[] chosen = Outbreak.cheapestTests(n, nb, costs);
                long sum = 0;
                for (int v = 0; v < n; v++) if (chosen[v]) sum += costs[v];
                check(sum == best, "minimum cover cost");
                for (int v = 1; v < n; v++) check(chosen[v] || chosen[parent[v]], "reconstructed cover");
            }
            int diameter = 0;
            for (int start = 0; start < n; start++) {
                int[] distance = new int[n]; Arrays.fill(distance, -1);
                ArrayDeque<Integer> q = new ArrayDeque<>(); q.add(start); distance[start] = 0;
                while (!q.isEmpty()) {
                    int v = q.remove(); diameter = Math.max(diameter, distance[v]);
                    for (int u : nb[v]) if (distance[u] == -1) {
                        distance[u] = distance[v] + 1; q.add(u);
                    }
                }
            }
            check(Outbreak.longestChain(n, nb) == diameter, "tree diameter");
        }
        System.out.println("PASS: 300 string cases, overlaps, repeated windows, and 300 exhaustive small-tree cases.");
    }
}
