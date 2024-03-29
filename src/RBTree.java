/**
 * @author ZBK
 * @date 2019/8/8 - 22:55
 */

import java.util.ArrayList;

/**
 * @program: RedBlackTree
 *
 * @description: RebBlackTree
 *
 * @author: Zbk
 *
 * @create: 2019-08-08 22:55
 **/
public class RBTree<K extends Comparable<K>, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;


    private class Node{
        public K key;
        public V value;
        public Node left, right;
        //标识这个节点是红色还是黑色
        public boolean color;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            //默认新创建节点红色
            //因为新加入的节点都要先融合,
            // 先设置成红色代表和红黑树中对应2-3树中的等价的节点进行融合
            color = RED;
        }
    }

    private Node root;
    private int size;

    public RBTree(){
        root = null;
        size = 0;
    }

    public int getSize(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    //判断这个节点的颜色
    //我们认为空节点不需要融合,所以设置为黑色
    private boolean isRed(Node node){
        if (node == null)
            return BLACK;
        return node.color;
    }

    // 向红黑树中添加新的元素(key, value)
    public void add(K key, V value){
        root = add(root, key, value);
        //添加完后,保持根节点为黑色节点
        root.color=BLACK;
    }

    //对传入的note,进行左旋转,将旋转之后新的子树的根返回回去
    //    //   node                     x
    //    //  /   \     左旋转         /  \
    //    // T1   x   --------->   node   T3
    //    //     / \              /   \
    //    //    T2 T3            T1   T2
    private Node leftRotat(Node node){
        Node x = node.right;

        //左旋
        node.right = x.left;
        x.left=node;

        //维持新的根节点颜色根之前根节点颜色一样
        x.color=node.color;
        node.color=RED;

        return x;
    }

    //     node                   x
    //    /   \     右旋转       /  \
    //   x    T2   ------->   y   node
    //  / \                       /  \
    // y  T1                     T1  T2
    private Node rightRotate(Node node) {
        Node x = node.left;

        //右旋转
        node.left=x.right;
        x.right=node;

        x.color=node.color;
        node.color=RED;

        return x;
    }

    /** 
    * @Description:  颜色反转
    * @Param: [node]node为根的包括他的两个孩子进行颜色反转
    * @return: void 
    * @Author: ZBK 
    * @Date: 2019/8/10 
    */ 
    private void flipColors(Node node){
        node.color = RED;
        node.left.color = node.right.color = BLACK;
        
    }

    // 向以node为根的红黑树中插入元素(key, value)，递归算法
    // 返回插入新节点后红黑树的根
    private Node add(Node node, K key, V value){

        if(node == null){
            size ++;
            return new Node(key, value);
        }

        if(key.compareTo(node.key) < 0)
            node.left = add(node.left, key, value);
        else if(key.compareTo(node.key) > 0)
            node.right = add(node.right, key, value);
        else // key.compareTo(node.key) == 0
            node.value = value;

        //天机完了,对颜色进行维护
        //首先针对左旋转,左旋转是因为noe.right为红色,要进行左旋转
        //但是要考虑到node.left,如果是node.left也为红,那么房东为黑
        //就要进行颜色翻转
        //因此我们这里设置为条件是满足左旋条件不满足颜色反转条件
        if(isRed(node.right) && !isRed(node.left))
            node=leftRotat(node);

        //针对是否进行右旋转,也就是node.left为红,node.left.left也为红
        //那就对node进行右旋转.
        //右旋完后,节点颜色通常是root:黑,left/right:red
        //要在之后的递归中处理颜色问题
        if (isRed(node.left) && isRed(node.left.left))
            node=rightRotate(node);

        //进行颜色翻转
        if(isRed(node.right) && isRed(node.left))
            flipColors(node);

        return node;
    }

    // 返回以node为根节点的二分搜索树中，key所在的节点
    private Node getNode(Node node, K key){

        if(node == null)
            return null;

        if(key.equals(node.key))
            return node;
        else if(key.compareTo(node.key) < 0)
            return getNode(node.left, key);
        else // if(key.compareTo(node.key) > 0)
            return getNode(node.right, key);
    }

    public boolean contains(K key){
        return getNode(root, key) != null;
    }

    public V get(K key){

        Node node = getNode(root, key);
        return node == null ? null : node.value;
    }

    public void set(K key, V newValue){
        Node node = getNode(root, key);
        if(node == null)
            throw new IllegalArgumentException(key + " doesn't exist!");

        node.value = newValue;
    }

    // 返回以node为根的二分搜索树的最小值所在的节点
    private Node minimum(Node node){
        if(node.left == null)
            return node;
        return minimum(node.left);
    }

    // 删除掉以node为根的二分搜索树中的最小节点
    // 返回删除节点后新的二分搜索树的根
    private Node removeMin(Node node){

        if(node.left == null){
            Node rightNode = node.right;
            node.right = null;
            size --;
            return rightNode;
        }

        node.left = removeMin(node.left);
        return node;
    }

    // 从二分搜索树中删除键为key的节点
    public V remove(K key){

        Node node = getNode(root, key);
        if(node != null){
            root = remove(root, key);
            return node.value;
        }
        return null;
    }

    private Node remove(Node node, K key){

        if( node == null )
            return null;

        if( key.compareTo(node.key) < 0 ){
            node.left = remove(node.left , key);
            return node;
        }
        else if(key.compareTo(node.key) > 0 ){
            node.right = remove(node.right, key);
            return node;
        }
        else{   // key.compareTo(node.key) == 0

            // 待删除节点左子树为空的情况
            if(node.left == null){
                Node rightNode = node.right;
                node.right = null;
                size --;
                return rightNode;
            }

            // 待删除节点右子树为空的情况
            if(node.right == null){
                Node leftNode = node.left;
                node.left = null;
                size --;
                return leftNode;
            }

            // 待删除节点左右子树均不为空的情况

            // 找到比待删除节点大的最小节点, 即待删除节点右子树的最小节点
            // 用这个节点顶替待删除节点的位置
            Node successor = minimum(node.right);
            successor.right = removeMin(node.right);
            successor.left = node.left;

            node.left = node.right = null;

            return successor;
        }
    }

    public static void main(String[] args){

        test();
    }

    static void test() {
        System.out.println("Pride and Prejudice");

        ArrayList<String> words = new ArrayList<>();
        if(FileOperation.readFile("pride-and-prejudice.txt", words)) {
            System.out.println("Total words: " + words.size());

            RBTree<String, Integer> map = new RBTree<>();
            for (String word : words) {
                if (map.contains(word))
                    map.set(word, map.get(word) + 1);
                else
                    map.add(word, 1);
            }

            System.out.println("Total different words: " + map.getSize());
            System.out.println("Frequency of PRIDE: " + map.get("pride"));
            System.out.println("Frequency of PREJUDICE: " + map.get("prejudice"));
        }

        System.out.println();
    }
}
