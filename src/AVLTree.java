/**
 * @author ZBK
 * @date 2019/8/1 - 23:57
 */

import java.util.ArrayList;

/**
 * @program: AVLTree
 *
 * @description:
 *
 * @author: Zbk
 *
 * @create: 2019-08-01 23:57
 **/
public class AVLTree<K extends Comparable<K>, V> {

    private class Node{
        public K key;
        public V value;
        public Node left, right;
        public int height;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            height=1;
        }
    }

    private Node root;
    private int size;

    public AVLTree(){
        root = null;
        size = 0;
    }

    //获得节点node的高度
    private int getHeight(Node node){
        if (node==null)
            return 0;
        return node.height;
    }

    public int getSize(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    //判断该二叉树是否为一颗二分搜索树
    public boolean isBST(){
        ArrayList<K> keys = new ArrayList<>();
        inOrder(root,keys);
        //我们这里inOrder是使用中序遍历,因为中序遍历是先遍历左子树,在遍历本身,再遍历右子树
        //因此中序遍历后放入arr中的元素是从小到大的,
        // 这样就可以根据元素是否从小到大而进行判断是否为二分搜索树
        for (int i=1;i<keys.size();i++){
            if (keys.get(i-1).compareTo(keys.get(i))>1)
                return false;
        }
        return true;
    }

    private void inOrder(Node node, ArrayList<K> keys) {
        if (node==null)
            return;

        inOrder(node.left, keys);
        keys.add(node.key);
        inOrder(node.right, keys);
    }

    //判断该二叉树是否是一颗平衡二叉树
    public boolean isBalanced(){
        return isBalanced(root);
    }

    private boolean isBalanced(Node node) {
        //d对于空树来说,是一个平衡二叉树
        if (node==null)
            return true;

        if (Math.abs(getBalanceFactor(node))>1)
            return false;
        return isBalanced(node.left) && isBalanced(node.right);
    }

    //右旋转
    // 对节点y进行向右旋转操作，返回旋转后新的根节点x
    //        y                              x
    //       / \                           /   \
    //      x   T4     向右旋转 (y)        z     y
    //     / \       - - - - - - - ->    / \   / \
    //    z   T3                       T1  T2 T3 T4
    //   / \
    // T1   T2
    private Node rightRotate(Node y){
        Node x = y.left;
        Node T3 = x.right;

        y.left=T3;
        x.right=y;

        //更新height,只用更新x,y因为其他的节点都没变
        y.height=Math.max(getHeight(y.left), getHeight(y.right))+1;
        x.height=Math.max(getHeight(x.left), getHeight(x.right))+1;

        return x;
    }


    // 对节点y进行向左旋转操作，返回旋转后新的根节点x
    //    y                             x
    //  /  \                          /   \
    // T1   x      向左旋转 (y)       y     z
    //     / \   - - - - - - - ->   / \   / \
    //   T2  z                     T1 T2 T3 T4
    //      / \
    //     T3 T4
    private Node leftRotate(Node y) {
        Node x = y.right;
        Node T2 = x.left;

        y.right=T2;
        x.left=y;

        y.height=Math.max(getHeight(y.left), getHeight(y.right))+1;
        x.height=Math.max(getHeight(x.left), getHeight(x.right))+1;

        return x;
    }


    // 向二分搜索树中添加新的元素(key, value)
    public void add(K key, V value){
        root = add(root, key, value);
    }

    // 向以node为根的二分搜索树中插入元素(key, value)，递归算法
    // 返回插入新节点后二分搜索树的根
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

        //更新Node的Height
        node.height=Math.max(getHeight(node.left), getHeight(node.right))+1;

        //计算平衡因子
        int balanceFactor = getBalanceFactor(node);
       /* //如果平衡因子的绝对值大于1,那么就不是一个平衡二叉树了
        if (Math.abs(balanceFactor)>1)
            System.out.println("unBalance"+balanceFactor);*/

        //平衡维护
        //LL如果左子树和右子树的高度差超过1的,并且,是左子树要高的
        if (balanceFactor>1 && getBalanceFactor(node.left)>=0)
            return rightRotate(node);

        //RR如果左子树和右子树的高度差超过1的,并且,是右子树要高的
        if (balanceFactor<-1 && getBalanceFactor(node.right)<=0)
            return  leftRotate(node);

        //LR
        //左子树要比右子树大至少2个单位,因此平衡因子大于1
        if (balanceFactor>1 && getBalanceFactor(node.left)<0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        //RL
        //右子树要比左子树大至少2个单位,因此平衡因子小于-1
        if (balanceFactor<-1 && getBalanceFactor(node.right)>0){
            node.right=rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }


    //计算每一个节点的平衡因子
    private int getBalanceFactor(Node node){
        if (node==null){
            return 0;
        }
        return getHeight(node.left)-getHeight(node.right);
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

        //声明retNode,就是最终要返回的node
        Node retNode;
        if( key.compareTo(node.key) < 0 ){
            node.left = remove(node.left , key);
            //将原本要返回的node,赋给retNode,先不要返回,一会要对retNode进行平衡维护
            retNode = node;
        }
        else if(key.compareTo(node.key) > 0 ){
            node.right = remove(node.right, key);
            //将原本要返回的node,赋给retNode,先不要返回,一会要对retNode进行平衡维护
            retNode = node;
        }
        else{   // key.compareTo(node.key) == 0

            // 待删除节点左子树为空的情况
            if(node.left == null){
                Node rightNode = node.right;
                node.right = null;
                size --;
                //将原本要返回的rightNode,赋给retNode,先不要返回,一会要对retNode进行平衡维护
                retNode = rightNode;
            }

            // 待删除节点右子树为空的情况
            else if(node.right == null){
                Node leftNode = node.left;
                node.left = null;
                size --;
                //将原本要返回的leftNode,赋给retNode,先不要返回,一会要对retNode进行平衡维护
                retNode = leftNode;
            }
            else {
                // 待删除节点左右子树均不为空的情况
                // 找到比待删除节点大的最小节点, 即待删除节点右子树的最小节点
                // 用这个节点顶替待删除节点的位置
                Node successor = minimum(node.right);
                successor.right = remove(node.right,successor.key);
                successor.left = node.left;

                node.left = node.right = null;

                //将原本要返回的successor这个node,赋给retNode,先不要返回,一会要对retNode进行平衡维护
                retNode = successor;
            }
        }

        if(retNode == null)
            return null;

        //现在retNode就是我们本来应该返回的node,但是此时要对retNode进行平衡操作
        //更新Node的Height
        retNode.height=Math.max(getHeight(retNode.left), getHeight(retNode.right))+1;

        //计算平衡因子
        int balanceFactor = getBalanceFactor(retNode);
       /* //如果平衡因子的绝对值大于1,那么就不是一个平衡二叉树了
        if (Math.abs(balanceFactor)>1)
            System.out.println("unBalance"+balanceFactor);*/

        //平衡维护
        //LL如果左子树和右子树的高度差超过1的,并且,是左子树要高的
        if (balanceFactor>1 && getBalanceFactor(retNode.left)>=0)
            return rightRotate(retNode);

        //RR如果左子树和右子树的高度差超过1的,并且,是右子树要高的
        if (balanceFactor<-1 && getBalanceFactor(retNode.right)<=0)
            return  leftRotate(retNode);

        //LR
        //左子树要比右子树大至少2个单位,因此平衡因子大于1
        if (balanceFactor>1 && getBalanceFactor(retNode.left)<0) {
            node.left = leftRotate(retNode.left);
            return rightRotate(retNode);
        }

        //RL
        //右子树要比左子树大至少2个单位,因此平衡因子小于-1
        if (balanceFactor<-1 && getBalanceFactor(retNode.right)>0){
            node.right=rightRotate(retNode.right);
            return leftRotate(retNode);
        }
        return retNode;
    }

    public static void main(String[] args){

        System.out.println("Pride and Prejudice");

        ArrayList<String> words = new ArrayList<>();
        if(FileOperation.readFile("pride-and-prejudice.txt", words)) {
            System.out.println("Total words: " + words.size());

            AVLTree<String, Integer> map = new AVLTree<>();
            for (String word : words) {
                if (map.contains(word))
                    map.set(word, map.get(word) + 1);
                else
                    map.add(word, 1);
            }

            System.out.println("Total different words: " + map.getSize());
            System.out.println("Frequency of PRIDE: " + map.get("pride"));
            System.out.println("Frequency of PREJUDICE: " + map.get("prejudice"));

            System.out.println("is RBTree"+map.isBST());

            System.out.println("is Balanced:"+map.isBalanced());

            for (String word:words){
                map.remove(word);
                if (!map.isBST()||!map.isBalanced())
                    throw new RuntimeException("Error");
            }
        }

        System.out.println();
    }
}