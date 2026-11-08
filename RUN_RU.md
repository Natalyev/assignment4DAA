# Как запустить

Открой PowerShell и выполни:

```powershell
cd "C:\Users\Admin\OneDrive\Desktop\DAA\patient-zero"
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Если java на этом компьютере зависает, используй прямой путь:

```powershell
& "C:\Program Files\Java\jdk-25\bin\javac.exe" -encoding UTF-8 -d out src/*.java
& "C:\Program Files\Java\jdk-25\bin\java.exe" -cp out Main
```

Открой output/outbreak.html двойным щелчком. Данные для ID 252119 уже в data.

Проверка примеров: `java tools/Check.java`.
Замеры: `java -cp out Bench`.

После проверки примеров снова запусти Main, чтобы HTML показывал твои данные.

## Перед сдачей

1. Разбери код и перепиши ответы README своими словами. В нём указана фактическая помощь ИИ.
2. Сохрани полноразмерный снимок output/outbreak.html как output/outbreak.png.
3. Создай пустой публичный репозиторий GitHub. Локальные коммиты уже есть.
4. Выполни команды, подставив свой адрес:

```powershell
git add README.md output/outbreak.png
git commit -m "Review report and add lab screenshot"
git remote add origin https://github.com/YOUR_NAME/patient-zero.git
git push -u origin main
```

5. Проверь доступность ссылки без входа и сдай её в Moodle.
