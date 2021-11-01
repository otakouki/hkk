#使うDockerイメージ
FROM mysql:8.0
#ポートを開ける
EXPOSE 3306
#MySQL設定ファイルをイメージ内にコピー
ADD ./my.cnf /etc/mysql/conf.d/my.cnf
#docker runに実行される
CMD ["mysqld"]

RUN useradd -m --uid 1000 --groups sudo hkk \
  && echo hkk:hkk_it_kaihatu | chpasswd

# 作成したユーザーに切り替える
USER hkk
