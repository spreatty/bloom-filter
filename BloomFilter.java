public class BloomFilter {
    private static double DEFAULT_ERROR_PROBABILITY = 0.01;

    private byte[] bits;
    private List<Function<Object, Integer>> hashes;

    public BloomFilter(int elementsCount) {
        this(elementsCount, DEFAULT_ERROR_PROBABILITY);
    }

    public BloomFilter(Collection<Object> collection) {
        this(collection, DEFAULT_ERROR_PROBABILITY);
    }

    public BloomFilter(Collection<Object> collection, double errorProbability) {
        this(collection.size(), errorProbability);
        collection.forEach(this::put);
    }

    public BloomFilter(int elementsCount, double errorProbability){
        int optimalBitCount = (int) (-elementsCount * Math.log(errorProbability) / Math.pow(Math.log(2), 2));
        int optimalHashCount = (int) (-Math.log(errorProbability) / Math.log(2));

        bits =  new byte[optimalBitCount];
        hashes = Stream.<Function<Object, Integer>>generate(() -> {
                    final int seed = (int) (Math.random() * bits.length);
                    return object -> Math.abs(object.hashCode() * seed) % bits.length;
                })
                .limit(optimalHashCount)
                .collect(Collectors.toList());
    }

    public void put(Object object){
        hashes.stream()
                .map(hash -> hash.apply(object))
                .forEach(idx -> bits[idx] = 1);
    }

    public boolean test(Object object){
        return hashes.stream()
                .map(hash -> hash.apply(object))
                .noneMatch(idx -> bits[idx] == 0);
    }
}
