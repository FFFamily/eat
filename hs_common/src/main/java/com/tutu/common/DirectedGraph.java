package com.tutu.common;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 有向图
 * @param <T> 图中节点的类型
 */
public class DirectedGraph<T> {
    // 有向图的邻接表
    private Map<T, List<T>> graph;
    /**
     * 初始化有向图
     * @return 有向图
     */
    public  static <T> DirectedGraph<T> init(){
        DirectedGraph<T> directedGraph = new DirectedGraph<>();
        directedGraph.graph = new HashMap<>();
        return directedGraph;
    }
    /**
     * 添加有向边
     * @param from 边的起点
     * @param to 边的终点
     */
    public void addEdge(T from, T to){
        graph.putIfAbsent(from, new ArrayList<>());
        graph.putIfAbsent(to, new ArrayList<>());
        graph.get(from).add(to);
    }
    /**
     * 获取邻居节点
     * @param vertex 顶点
     * @return 邻居节点列表
     */
    public List<T> getNeighbors(T vertex){
        return graph.getOrDefault(vertex, new ArrayList<>());
    }

    /**
     * 分层拓扑排序
     * @return 分层拓扑排序后的顶点列表
     */
    public List<List<T>> layeredTopologicalSort(){
        // 计算每个节点的入度
        Map<T, Integer> indegree = new HashMap<>();
        // 节点的入度为0
        for(T vertex : graph.keySet()){
            indegree.put(vertex, 0);
        }
        // 遍历所有节点，更新入度
        for(T vertex : graph.keySet()){
            for(T neighbor : getNeighbors(vertex)){
                indegree.put(neighbor, indegree.get(neighbor) + 1);
            }
        }
        // 入度为0的节点加入队列
        Queue<T> queue = new LinkedList<>();
        for(T vertex : graph.keySet()){
            if(indegree.get(vertex) == 0){
                queue.add(vertex);
            }
        }
        List<List<T>> result = new ArrayList<>();
        while(!queue.isEmpty()){
            List<T> list = new ArrayList<>();
            while(!queue.isEmpty()){
                T vertex = queue.poll();
                list.add(vertex);
            }
            for (T item : list) {
                for(T neighbor : getNeighbors(item)){
                    indegree.put(neighbor, indegree.get(neighbor) - 1);
                    if(indegree.get(neighbor) == 0){
                        queue.add(neighbor);
                    }
                }
            }
            result.add(list);
        }
        int resultSize = result.stream().flatMap(List::stream).toList().size();
        if (resultSize != graph.size()){
            throw new IllegalArgumentException("图中存在环，无法进行拓扑排序");
        }
        return result;
    }

    /**
     * 根据子顶点获取整个邻接表
     * @param vertex 子顶点
     * @return 子图
     */
    public Map<T, List<T>> getGraphBySubVertex(T vertex){
        Map<T, List<T>> result = new HashMap<>();
        List<T> form = getReachableFrom(vertex);
    }

    /**
     * 从指定节点出发，找出所有能到达的节点（前向推理）
     */
    public List<T> getReachableFrom(T vertex){
        List<T> reachable = new ArrayList<>();
        dfsReachable(vertex, reachable);
        return reachable;
    }
    private void dfsReachable(T v, List<T> visited) {
        if (visited.contains(v)) return;
        visited.add(v);
        for (T neighbor : getNeighbors(v)) {
            dfsReachable(neighbor, visited);
        }
    }


}
