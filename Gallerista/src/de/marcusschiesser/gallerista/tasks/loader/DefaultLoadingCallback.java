package de.marcusschiesser.gallerista.tasks.loader;


public abstract class DefaultLoadingCallback implements
		LoadingCallback {
	private BitmapWorkerTask mBitmapWorkerTaskReference = null;

	@Override
	public void setBitmapWorkerTask(BitmapWorkerTask task) {
		mBitmapWorkerTaskReference = task;
	}

	@Override
	public BitmapWorkerTask getBitmapWorkerTask() {
		return mBitmapWorkerTaskReference;
	}

}