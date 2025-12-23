import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np
from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans, AgglomerativeClustering
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier, plot_tree
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, silhouette_score

# 1. 数据加载与预处理
# ---------------------------------------------------------
iris = datasets.load_iris()
X = iris.data
y = iris.target
feature_names = iris.feature_names
target_names = iris.target_names

# 为了方便可视化，我们创建一个DataFrame
df = pd.DataFrame(X, columns=feature_names)
df['species'] = y

# 数据标准化（这对K-Means和逻辑回归很重要）
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# 设置绘图风格
sns.set(style="whitegrid")
plt.rcParams['font.sans-serif'] = ['SimHei'] # 用来正常显示中文标签，如果是Mac或Colab可能需要调整
plt.rcParams['axes.unicode_minus'] = False

print("=== 实验二：鸢尾花数据集分析 ===")
print(f"样本总数: {len(df)}")
print(f"特征: {feature_names}\n")

# =========================================================
# 任务一：聚类分析 (Clustering)
# 建议算法：K-Means, 层次聚类 
# =========================================================

print("--- 聚类分析结果 ---")

# --- A. K-Means 聚类 ---
kmeans = KMeans(n_clusters=3, random_state=42, n_init=10)
y_kmeans = kmeans.fit_predict(X_scaled)
silhouette_kmeans = silhouette_score(X_scaled, y_kmeans)
print(f"[K-Means] 轮廓系数 (Silhouette Score): {silhouette_kmeans:.4f}")

# --- B. 层次聚类 (Hierarchical Clustering) ---
hc = AgglomerativeClustering(n_clusters=3)
y_hc = hc.fit_predict(X_scaled)
silhouette_hc = silhouette_score(X_scaled, y_hc)
print(f"[层次聚类] 轮廓系数 (Silhouette Score): {silhouette_hc:.4f}")

# --- 聚类可视化 (使用花瓣长度和宽度) ---
fig, axes = plt.subplots(1, 3, figsize=(18, 5))

# 原始标签
sns.scatterplot(data=df, x=feature_names[2], y=feature_names[3], hue='species', palette='viridis', ax=axes[0])
axes[0].set_title('原始数据分布 (Ground Truth)')

# K-Means 结果
axes[1].scatter(X[:, 2], X[:, 3], c=y_kmeans, cmap='viridis', edgecolor='k', s=50)
axes[1].set_title(f'K-Means 聚类结果\n轮廓系数: {silhouette_kmeans:.2f}')
axes[1].set_xlabel(feature_names[2])
axes[1].set_ylabel(feature_names[3])

# 层次聚类结果
axes[2].scatter(X[:, 2], X[:, 3], c=y_hc, cmap='viridis', edgecolor='k', s=50)
axes[2].set_title(f'层次聚类结果\n轮廓系数: {silhouette_hc:.2f}')
axes[2].set_xlabel(feature_names[2])
axes[2].set_ylabel(feature_names[3])

plt.tight_layout()
plt.show() # 请保存这张图作为“图1：聚类效果对比”


# =========================================================
# 任务二：分类分析 (Classification)
# 要求：7:3 划分训练集和测试集 
# 建议算法：逻辑回归, 决策树 
# =========================================================

print("\n--- 分类分析结果 ---")

# 划分数据集 (70% 训练, 30% 测试)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)
# 对逻辑回归使用标准化后的数据
X_train_scaled, X_test_scaled, y_train_scaled, y_test_scaled = train_test_split(X_scaled, y, test_size=0.3, random_state=42)

# --- A. 逻辑回归 (Logistic Regression) ---
log_reg = LogisticRegression(random_state=42, max_iter=200)
log_reg.fit(X_train_scaled, y_train_scaled)
y_pred_log = log_reg.predict(X_test_scaled)
acc_log = accuracy_score(y_test_scaled, y_pred_log)

print(f"[逻辑回归] 测试集准确率: {acc_log:.4f}")
print("分类报告:\n", classification_report(y_test_scaled, y_pred_log, target_names=target_names))

# --- B. 决策树 (Decision Tree) ---
dtree = DecisionTreeClassifier(random_state=42, max_depth=3) # 限制深度防止过拟合
dtree.fit(X_train, y_train)
y_pred_tree = dtree.predict(X_test)
acc_tree = accuracy_score(y_test, y_pred_tree)

print(f"[决策树] 测试集准确率: {acc_tree:.4f}")
print("分类报告:\n", classification_report(y_test, y_pred_tree, target_names=target_names))

# --- 分类可视化 (混淆矩阵) ---
fig, axes = plt.subplots(1, 2, figsize=(12, 5))

# 逻辑回归混淆矩阵
sns.heatmap(confusion_matrix(y_test_scaled, y_pred_log), annot=True, fmt='d', cmap='Blues', ax=axes[0],
            xticklabels=target_names, yticklabels=target_names)
axes[0].set_title(f'逻辑回归 混淆矩阵 (Acc: {acc_log:.2f})')
axes[0].set_ylabel('真实标签')
axes[0].set_xlabel('预测标签')

# 决策树混淆矩阵
sns.heatmap(confusion_matrix(y_test, y_pred_tree), annot=True, fmt='d', cmap='Greens', ax=axes[1],
            xticklabels=target_names, yticklabels=target_names)
axes[1].set_title(f'决策树 混淆矩阵 (Acc: {acc_tree:.2f})')
axes[1].set_ylabel('真实标签')
axes[1].set_xlabel('预测标签')

plt.tight_layout()
plt.show() # 请保存这张图作为“图2：分类混淆矩阵”

# --- 决策树可视化 (可选，用于报告分析) ---
plt.figure(figsize=(12, 8))
plot_tree(dtree, feature_names=feature_names, class_names=target_names, filled=True)
plt.title("决策树结构可视化")
plt.show() # 请保存这张图作为“图3：决策树结构”