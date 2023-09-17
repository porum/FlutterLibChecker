import sqlite3
import csv

# 连接到SQLite数据库（如果不存在，将会创建一个）
conn = sqlite3.connect('app-database.db')

# 创建一个新的Cursor对象
cur = conn.cursor()

# 创建一个新的表
cur.execute('''
    CREATE TABLE IF NOT EXISTS VersionEntity(
        release_date TEXT PRIMARY KEY NOT NULL,
        channel TEXT NOT NULL,
        version TEXT NOT NULL,
        dart_sdk_version TEXT NOT NULL,
        engine_commit TEXT NOT NULL,
        snapshot_hash TEXT NOT NULL
    )
''')

# 打开CSV文件并读取内容
with open('enginehash.tmp.csv', 'r') as csv_file:
    csv_reader = csv.reader(csv_file)

    # 跳过标题行（如果有）
    next(csv_reader)


    # 将CSV文件的每一行插入到表中
    for row in csv_reader:
        cur.execute('''
            INSERT INTO VersionEntity VALUES (?, ?, ?, ?, ?, ?)
        ''', row)

# 提交事务
conn.commit()

# 关闭连接
conn.close()