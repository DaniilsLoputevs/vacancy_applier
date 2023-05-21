# vacancy_applier


# Как запускать?

Этот проект запускается ТОЛЬКО через IDEA, только из Исходного кода на Kotlin!!! Только Хардкор!
Необходимы минимальные знания синтаксиса ЯП Kotlin

Идём в файлы:

* [ConfigExample.kt](src/main/kotlin/jobs/personal/ConfigExample.kt) - описание параметров конфига
* [Config.kt](src/main/kotlin/jobs/personal/Config.kt) - пример конфига. (можно переписывать под себя или
  копировать файл)

В этих файлах указа Чувствительная информация для Запуска. Например, email & password для HH.ru

### Важно!!!!
* Заполните все парамеры конфига своими данными перед запуском! По умолчанию стоят фейвокые данные(email, password и т.д.)
Так не запустится.
* Выберите браузер который у вас установлен, иначе не запустится.


Далее идём в файл [RunExample.kt](src/test/kotlin/jobs/RunExample.kt)

```private val workDirPath = "C:/Users/user/Desktop/vacancy_applier"```

В этой строчке указываем путь к папке с которой будет работать эта программа.
Рекомендуется выделить под это отдельную папку.

Папка используется для хранения:
* Логов запусков программы.
* Списка вакансий на которые уже была подан Отклик.

```
    @Test fun runExampleHH() {
        Session(workDirPath) {
            exe(PrimaryPipelineHH, ExampleConfigHH)
        }
    }
```
Запускаем Тест из IDEA

Откроется Окно браузера с URL логин на HH.ru
Ждём пока программа вставит email & password из конфига и нажмёт "Войти".
Далее появится Captcha и тут нужно вас самим, РУЧКАМИ! Её заполнить и нажать "Войти" повторно.
п.с. Да, Робот не может пройти Captcha... Кина не будет, свет вырубили ещё до восстания машин.

Радуемся!!! ^_^

Ну или пишем автору в [Телеграм](https://t.me/Daniils_Loputevs) "а у меня не работает, а хочу что бы работало!"
Так же не забываем скинуть последний лог файла из папки с логами.

П.с. эта программа не имеет Main класса и запускается только как test case, как скрипт с пред настройками из IDEA.


[//]: # (<details>)

[//]: # (<summary>World</summary>)

[//]: # (<blockquote>)

[//]: # (    :smile:)

[//]: # (</blockquote>)

[//]: # (</details>)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (<details><summary>World</summary>)

[//]: # ()
[//]: # (    :smile:)

[//]: # ()
[//]: # (</details>)






[//]: # ()
[//]: # (<details>)

[//]: # (sdfasfasf)

[//]: # (</details>)

[//]: # ()
[//]: # (<ol>)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # ([//]: # &#40;<li> &#41;)
[//]: # ([//]: # &#40;<details>&#41;)
[//]: # ([//]: # &#40;<summary>Hello</summary>&#41;)
[//]: # ([//]: # &#40;<blockquote>&#41;)
[//]: # (  <details>)

[//]: # (sgasgasgasg)

[//]: # (</details>)

[//]: # ()
[//]: # ([//]: # &#40;</blockquote>&#41;)
[//]: # ()
[//]: # ([//]: # &#40;</details>&#41;)
[//]: # ()
[//]: # ([//]: # &#40;</li>&#41;)
[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (<li> <details><summary>Hello</summary><blockquote>)

[//]: # (  <details><summary>World</summary><blockquote>)

[//]: # (    :smile:)

[//]: # (  </blockquote></details>)

[//]: # (</blockquote></details>)

[//]: # (</li>)

[//]: # (<li> <details><summary>Hello</summary><blockquote>)

[//]: # (  <details><summary>World</summary><blockquote>)

[//]: # (    :smile:)

[//]: # (  </blockquote></details>)

[//]: # (</blockquote></details>)

[//]: # (</li>)

[//]: # (</ol>)