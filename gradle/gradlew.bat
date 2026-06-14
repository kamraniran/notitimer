            @echo off
            if "%DEBUG_градле%" == "" (
                REM Disable echo
                setlocal
            ) else (
                REM Enable echo
                setlocal enable-echo
            )

            set DIRNAME=%~dp0
            if "%DIRNAME%" == "" set DIRNAME=.
            set APP_BASE_NAME=%~n0
            set APP_HOME=%DIRNAME%
            set JAVA_EXE=java

            REM Use the correct Windows separator for the classpath
            set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

            REM Check if GRADLE_EXIT_CONSOLE is set, if so, don't display output
            REM See https://github.com/gradle/gradle/issues/17274
            if not "%GRADLE_EXIT_CONSOLE%" == "" goto main

            REM Display the Gradle banner.
            @echo ===============================================================================
            @echo ==                    Gradle: A Build Tool                     ==
            @echo ===============================================================================

            :main
            "%JAVA_HOME%\bin\java" %JAVA_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %1 %2 %3 %4 %5 %6 %7 %8 %9

            set EXIT_CODE=%ERRORLEVEL%
            if %EXIT_CODE% == 0 goto main_exit

            REM If error occurred, display error message and exit with non-zero code
            echo.
            echo FAILURE: Build failed with an exception.
            echo.
            echo  * Where:
            echo  * What went wrong:
            echo  * Try:
            echo  See .//gradlew.bat for instruction.
            echo Run with --stacktrace option to get the stack trace.
            echo Run with --info or --debug option to get more log output.
            echo.
            goto main_exit

            :main_exit
            endlocal
            exit /b %EXIT_CODE%
