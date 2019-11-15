package scraping;

public class Errors {
	public static class NoNameException extends Exception {
		private final String msg;
		public NoNameException(String msg) { this.msg = msg; }
		public NoNameException() { this(""); }
		public String toString() {
			return "scraping.Errors.NoNameException: Failed to get name of law " + msg;
		}
	}
	
	public static class NoDateException extends Exception {
		private final String msg;
		public NoDateException(String msg) { this.msg = msg; }
		public NoDateException() { this(""); }
		public String toString() {
			return "scraping.Errors.NoDateException: Failed to get name of law " + msg;
		}
	}
	
	public static class NoJsonException extends Exception {
		private final String msg;
		public NoJsonException(String msg) { this.msg = msg; }
		public NoJsonException() { this(""); }
		public String toString() {
			return "scraping.Errors.NoJsonException: Failed to get name of law " + msg;
		}
	}
	
	public static class NoUrlException extends Exception {
		private final String msg;
		public NoUrlException(String msg) { this.msg = msg; }
		public NoUrlException() { this(""); }
		public String toString() {
			return "scraping.Errors.NoUrlException: Failed to get name of law " + msg;
		}
	}
	
	public static class BadDataException extends Exception {
		private final String msg;
		public BadDataException(String msg) { this.msg = msg; }
		public BadDataException() { this(""); }
		public String toString() {
			return "scraping.Errors.BadDataException: Failed to get name of law " + msg;
		}
	}
}
