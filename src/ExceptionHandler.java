public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Kesalahan: " + e.getMessage());
        System.out.println("Tracing: ");
        e.printStackTrace();
    }
}
